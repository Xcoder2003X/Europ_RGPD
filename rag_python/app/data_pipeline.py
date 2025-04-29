import logging
from langchain_community.document_loaders import UnstructuredURLLoader, PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter

logging.basicConfig(level=logging.INFO)

URLS = [
    "https://www.dpo-partage.fr/exemple-rapport-annuel-du-dpo/",
    "https://www.cnil.fr/fr/cybersecurite/les-violations-de-donnees-personnelles",
    "https://www.cnil.fr/sites/cnil/files/2024-03/cnil_guide_securite_personnelle_2024.pdf",
    "https://www.mission-rgpd.com/",
    "https://reports.alpiq.com/20/fr/conformite-socio-economique/",
    "https://lecoursgratuit.com/rapport-rgpd-modele-excel-automatise/",
    "https://www.witik.io/lp/modele-rapport-annuel-dpo/",
    "https://www.dpo-partage.fr/exemple-rapport-annuel-du-dpo/?utm_source=chatgpt.com"

]

# Classe principale pour le pipeline de traitement des données.
class DataPipeline:
    def __init__(self, urls=None):
        self.urls = urls or URLS
        self.loaders = []  # will now hold (loader, url) pairs


    #Crée les loaders appropriés selon le type de document (PDF ou HTML).
    def build_loaders(self):
        self.loaders = []
        for url in self.urls:
            if url.lower().endswith('.pdf'):
                loader = PyPDFLoader(url)
            else:
                loader = UnstructuredURLLoader([url])
            # store the mapping here
            self.loaders.append((loader, url))


    #Charge les documents et ajoute les métadonnées de source. Gestion d'erreurs robuste.
    def load_documents(self):
        all_docs = []
        self.build_loaders()
        for loader, source_url in self.loaders:
            try:
                docs = loader.load()
                for doc in docs:
                    # now this is always safe:
                    doc.metadata['source'] = source_url
                all_docs.extend(docs)
            except Exception as e:
                logging.warning(f"Failed to load {source_url!r}: {e}")
        return all_docs


    #Découpe les documents en chunks avec chevauchement et structure sémantique préservée.
    @staticmethod
    def split_documents(docs, chunk_size=800, chunk_overlap=150):
        splitter = RecursiveCharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap,
            separators=["\nArticle", "\n§", "\n•", "\n\n", "\n"]
        )
        chunks = []
        for doc in docs:
            pieces = splitter.split_text(doc.page_content)
            for i, text in enumerate(pieces):
                chunks.append({
                    'text': text,
                    'metadata': {
                        'source': doc.metadata.get('source'),
                        'chunk_index': i
                    }
                })
        return chunks

    def run(self):
        docs = self.load_documents()
        return self.split_documents(docs)


