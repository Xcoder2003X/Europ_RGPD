# test_rag.py
import unittest
import numpy as np
from rag_core.ragcore import RAGSystem

class TestRAGResults(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        """Initialisation du système RAG une fois pour tous les tests"""
        cls.rag = RAGSystem()
        cls.sample_question = "Quel est le délai de notification d'une violation de données selon la CNIL ?"
        
    def test_embeddings_characteristics(self):
      """Vérification des caractéristiques des embeddings"""
    # Exécuter la méthode de debug
      self.rag.debug_embeddings("test d'embedding")
    
      embedding = self.rag.embedder.embed_query("test")
    
    # Test dimensionnel
      self.assertEqual(len(embedding), 768)
    
    # Test statistique avec tolérance ±0.05
      mean = np.mean(embedding)
      std = np.std(embedding)
    
    # Vérification moyenne
      self.assertTrue(
          0.0324 - 0.05 <= mean <= 0.0324 + 0.05,
          f"Moyenne anormale: {mean:.4f} (attendue ≈0.0324 ±0.05)"
      )
    
    # Vérification écart-type
      self.assertTrue(
          0.0712 - 0.05 <= std <= 0.0712 + 0.05,
          f"Écart-type anormal: {std:.4f} (attendu ≈0.0712 ±0.05)"
      )


        
    def test_query_results_structure(self):
        """Vérification de la structure des résultats"""
        results = self.rag.query(self.sample_question)
        
        # Vérifier la présence des clés obligatoires
        required_keys = ['text', 'source', 'relevance', 'chunk_index']
        for result in results:
            for key in required_keys:
                self.assertIn(key, result, 
                            f"Clé manquante: {key} dans {result.keys()}")
                
    def test_relevance_scores(self):
        """Validation des scores de pertinence"""
        results = self.rag.query(self.sample_question)
        
        for result in results:
            relevance = result['relevance']
            self.assertGreaterEqual(relevance, 0.0,
                                  f"Pertinence négative: {relevance}")
            self.assertLessEqual(relevance, 1.0,
                               f"Pertinence >100%: {relevance}")
            
    def test_expected_content(self):
        """Vérification du contenu attendu"""
        results = self.rag.query(self.sample_question, top_k=3)
        
        # Vérifier la présence du délai de 72h dans au moins un résultat
        found = False
        for result in results:
            if "72 heures" in result['text']:
                found = True
                break
                
        self.assertTrue(found, "La mention '72 heures' n'a pas été trouvée dans les résultats")

    def test_source_files(self):
        """Vérification des sources des documents"""
        results = self.rag.query(self.sample_question)
        allowed_sources = [
            'les_violations_de_donnees_personnelles.html',
            'cnil_guide_2024.pdf',
            'documentindex.html'
        ]
        
        for result in results:
            self.assertIn(result['source'], allowed_sources,
                         f"Source inconnue: {result['source']}")

if __name__ == '__main__':
    unittest.main(verbosity=2)