import logging
import faiss
import numpy as np
from langchain_huggingface import HuggingFaceEmbeddings
from data_pipeline import DataPipeline

logging.basicConfig(level=logging.INFO)

class RAGSystem:

    #Initialisation du système RAG avec modèle d'embedding CamemBERT et stockage FAISS
    def __init__(self, persist_path="vector_store/legal_index.faiss"):
        self.embedder = HuggingFaceEmbeddings(model_name="dangvantuan/sentence-camembert-base")
        self.index = None
        self.chunks = []
        self.persist_path = persist_path
        self._initialize()

    def _initialize(self):
        pipeline = DataPipeline()
        raw_chunks = pipeline.run()
        self.chunks = raw_chunks

        texts = [c['text'] for c in raw_chunks]
        embeddings = self.embedder.embed_documents(texts)

        dim = embeddings[0].shape[-1] if hasattr(embeddings[0], 'shape') else len(embeddings[0])
        self.index = faiss.IndexFlatL2(dim)
        self.index.add(np.array(embeddings))
        faiss.write_index(self.index, self.persist_path)
        logging.info(f"FAISS index built with {len(texts)} vectors")

    #Recherche de similarité et formatage des résultats avec métriques de pertinence
      
    def query(self, question, top_k=4):
        q_embed = self.embedder.embed_query(question)
        distances, ids = self.index.search(np.array([q_embed]), top_k)

        results = []
        assembled = []
        max_dist = distances.max() if distances.size else 1
        for dist, idx in zip(distances[0], ids[0]):
            chunk = self.chunks[idx]
            relevance = 1 - (dist / max_dist)
            results.append({
                'text': chunk['text'],
                'source': chunk['metadata']['source'],
                'chunk_index': chunk['metadata']['chunk_index'],
                'relevance': round(relevance, 2)
            })
            assembled.append(chunk['text'])

        return "\n".join(assembled), results
