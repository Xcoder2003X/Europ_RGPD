# data_pipeline/pipeline.py
import os
import logging
import re
import requests
from pathlib import Path
from urllib.parse import urlparse
from langchain_community.document_loaders import PyPDFLoader, UnstructuredURLLoader
from unstructured.partition.auto import partition  # Nouveau
from langchain.text_splitter import RecursiveCharacterTextSplitter
from fake_useragent import UserAgent
from langchain_core.documents import Document  # Import ajouté

logging.basicConfig(level=logging.INFO)

URLS = [
    "https://www.cnil.fr/fr/cybersecurite/les-violations-de-donnees-personnelles",
    "https://www.cnil.fr/sites/cnil/files/2024-03/cnil_guide_securite_personnelle_2024.pdf",
    "https://www.mission-rgpd.com/",
    "https://reports.alpiq.com/20/fr/conformite-socio-economique/",
    "https://www.witik.io/lp/modele-rapport-annuel-dpo/",
    "https://www.dpo-partage.fr/exemple-rapport-annuel-du-dpo/?utm_source=chatgpt.com"

]


class DataPipeline:
    def __init__(self, cache_dir="document_cache"):
        self.ua = UserAgent()
        self.cache_dir = Path(cache_dir)
        self.cache_dir.mkdir(parents=True, exist_ok=True)
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        })

    def _sanitize_filename(self, url):
      parsed = urlparse(url)
      path = parsed.path.split('/')[-1] or "document"
      path = re.sub(r'\?.*', '', path)  # Supprime les paramètres
      path = re.sub(r'[^a-zA-Z0-9]', '_', path)
    
    # Ajout d'extension si manquante
      if '.' not in path:
          if parsed.path.endswith('/'):
              path += "index.html"
          else:
              path += ".html"
    
      return self.cache_dir / path
    


    def _download_file(self, url):
        try:
            headers = {
                'User-Agent': self.ua.random,  # <-- Génère un User-Agent réaliste
                'Accept-Language': 'fr-FR,fr;q=0.9',
                'Referer': 'https://www.google.com/'
            }
            response = self.session.get(url, headers=headers, timeout=15)

            response.encoding = 'utf-8'
            response.raise_for_status()
            
            file_path = self._sanitize_filename(url)
            mode = 'wb' if url.lower().endswith('.pdf') else 'w'
            
            with open(file_path, mode, encoding='utf-8' if mode == 'w' else None) as f:
                if mode == 'wb':
                    f.write(response.content)
                else:
                    f.write(response.text)
            
            return str(file_path)
        except Exception as e:
            logging.error(f"Échec du téléchargement: {url} - {str(e)}")
            return None

    def _process_file(self, file_path):
      
      try:
          # Détection manuelle du type de fichier
          if file_path.endswith('.pdf'):
              loader = PyPDFLoader(file_path)
              return loader.load()
          else:
            # Utilisation de la nouvelle API Unstructured
              elements = partition(filename=file_path)
              return [
                Document(
                    page_content=element.text,
                    metadata={"source": file_path}
                ) 
                for element in elements
                ]
      except Exception as e:
              logging.error(f"Erreur de traitement: {file_path} - {str(e)}")
              return []

    @staticmethod
    def split_documents(docs, chunk_size=800, chunk_overlap=150):
        splitter = RecursiveCharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap,
            separators=["\nArticle", "\n§", "\n•", "\n\n", "\n"]
        )
        return [
            {
                'text': text,
                'metadata': {
                    'source': doc.metadata.get('source'),
                    'chunk_index': i,
                    'score': 0.0  # Initialisé à 0
                }
            }
            for doc in docs
            for i, text in enumerate(splitter.split_text(doc.page_content))
        ]

    def run(self):
        all_docs = []
        for url in URLS:
            cached_path = self._download_file(url)
            if cached_path:
                docs = self._process_file(cached_path)
                all_docs.extend(docs)
        return self.split_documents(all_docs)

if __name__ == "__main__":
    pipeline = DataPipeline()
    result = pipeline.run()
    print(f"{len(result)} chunks générés avec succès!")