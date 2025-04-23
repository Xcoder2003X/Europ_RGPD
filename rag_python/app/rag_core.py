import logging
logging.basicConfig(level=logging.DEBUG)
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_huggingface.embeddings import HuggingFaceEmbeddings
import faiss

import numpy as np

class RAGSystem:
    def __init__(self):
        # Charger le modèle CamemBERT qui Turn user questions 
        # (and documents) into vector embeddings (numeric form) so they can be compared efficiently
        self.embedder = HuggingFaceEmbeddings(
            model_name="dangvantuan/sentence-camembert-base"
        )
        
        # Initialiser l'index FAISS
        self.index = None
        self.chunks = []
        
        # Exécuter le pipeline de données au démarrage
        self.initialize_rag()

    def initialize_rag(self):
        # Étape 1 : Extraction des données
        from data_pipeline import run_data_pipeline
        raw_texts = run_data_pipeline()
        logging.debug(raw_texts)

        # Étape 2 : Découpage en chunks optimise pour l'analyse RGPD
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=800,  # Réduit pour plus de précision
            chunk_overlap=150,  # Ajusté pour maintenir le contexte
            separators=[
                "\nArticle",  # Pour les articles RGPD
                "\n§",        # Pour les sections
                "\n•",        # Pour les listes
                "\n\n",       # Pour les paragraphes
                "\n",         # Pour les lignes
                " ",          # Pour les mots
            ],
            length_function=len,
            is_separator_regex=False,
        )
        
        self.chunks = []
        for text in raw_texts:
            # Nettoyage préliminaire du texte
            text = text.replace('\r', '\n').replace('\t', ' ')
            text = ' '.join(text.split())  # Normalisation des espaces
            
            # Découpage du texte
            chunks = text_splitter.split_text(text)
            self.chunks.extend(chunks)
        
        # Étape 3 : Vectorisation avec dimensionnalité optimisée
        # Converts the text chunks into embeddings
        embeddings = self.embedder.embed_documents(self.chunks)
        dimension = len(embeddings[0])
        self.index = faiss.IndexFlatL2(dimension)
        #Adds these embeddings to a FAISS index
        #FAISS permet de retrouver très vite "quel vecteur 
        # ressemble le plus" à ta question.
        self.index.add(np.array(embeddings))
        
        # Sauvegarder l'index
        #Saves the FAISS index to legal_index.faiss
        faiss.write_index(self.index, "vector_store/legal_index.faiss")

    def query(self, question, k=4):  # Augmenté à 4 pour plus de contexte
        # Embedding de la question
        query_embedding = self.embedder.embed_query(question)
        distances, indices = self.index.search(np.array([query_embedding]), k)
        
        # Récupérer les chunks pertinents avec pondération par distance
        relevant_chunks = []
        for i, idx in enumerate(indices[0]):
            chunk = self.chunks[idx]
            # Ajouter un indicateur de pertinence basé sur la distance
            relevance = 1 - (distances[0][i] / max(distances[0]))
            relevant_chunks.append(f"[Pertinence: {relevance:.2f}] {chunk}")
        
        return " ".join(relevant_chunks), relevant_chunks
    

        