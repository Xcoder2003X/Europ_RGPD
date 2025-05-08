import logging
import json
import os
import numpy as np
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS
from data_pipeline.pipeline import DataPipeline

logging.basicConfig(level=logging.INFO)

class RAGSystem:
    def __init__(self, persist_dir="vector_store"):
        self.embedder = HuggingFaceEmbeddings(
            model_name="dangvantuan/sentence-camembert-base",
            model_kwargs={'device': 'cpu'},
            encode_kwargs={'normalize_embeddings': True}
        )
        self.persist_dir = persist_dir
        self.vector_store = None
        
        if not self._load_existing_store():
            self._build_new_store()

    def _load_existing_store(self):
        if os.path.exists(os.path.join(self.persist_dir, "index.faiss")):
            try:
                self.vector_store = FAISS.load_local(
                    folder_path=self.persist_dir,
                    embeddings=self.embedder,
                    allow_dangerous_deserialization=True
                )
                logging.info("Index FAISS chargé avec succès")
                return True
            except Exception as e:
                logging.error(f"Erreur de chargement: {str(e)}")
                return False
        return False

    def _build_new_store(self):
        logging.info("Début de la construction de l'index...")
        
        pipeline = DataPipeline()
        chunks = pipeline.run()
        
        if not chunks:
            raise RuntimeError("Aucun chunk valide généré par le pipeline")
            
        texts = [c['text'] for c in chunks]
        metadatas = [c['metadata'] for c in chunks]
        
        try:
            self.vector_store = FAISS.from_texts(
                texts=texts,
                embedding=self.embedder,
                metadatas=metadatas
            )
            self.vector_store.save_local(self.persist_dir)
            logging.info(f"Index créé avec {len(chunks)} embeddings")
        except Exception as e:
            logging.error(f"Échec de la création de l'index: {str(e)}")
            raise

    def query(self, question, top_k=5):
        if not self.vector_store:
            raise RuntimeError("Index non initialisé")
            
        # Récupération des résultats avec scores
        docs_and_scores = self.vector_store.similarity_search_with_score(question, k=top_k)
        
        return self._format_response(docs_and_scores)

    def _format_response(self, docs_and_scores):
        """Structure la réponse pour l'API"""
        try:
            assembled_text = []
            sources = []
            
            for doc, score in docs_and_scores:
                assembled_text.append(doc.page_content)
                sources.append({
                    "source": os.path.basename(doc.metadata.get('source', 'inconnu')),
                    "score": float(score),
                    "chunk_index": doc.metadata.get('chunk_index', -1)
                })
            
            return {
                "answer": "\n\n".join(assembled_text),
                "sources": sources
            }
            
        except Exception as e:
            logging.error(f"Erreur de formatage: {str(e)}")
            raise ValueError("Format de réponse invalide")

    def debug_embeddings(self, text_sample):
        """Méthode de debug pour les embeddings"""
        embedding = self.embedder.embed_query(text_sample)
        print(f"Dimension des embeddings: {len(embedding)}")
        print(f"Valeurs moyennes: {np.mean(embedding):.4f} ± {np.std(embedding):.4f}")

if __name__ == "__main__":
    rag = RAGSystem()
    print(rag.query("Qu'est-ce qu'une violation de données RGPD ?"))