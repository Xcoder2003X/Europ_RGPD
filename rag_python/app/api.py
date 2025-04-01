from flask import Flask, request, jsonify
from rag_core import RAGSystem
import os

app = Flask(__name__)
rag_system = RAGSystem()

@app.route('/query', methods=['POST'])
def query():
    try:
        data = request.get_json()
        question = data.get('question')
        
        if not question:
            return jsonify({'error': 'No question provided'}), 400
            
        # Get relevant chunks from RAG
        combined_text, chunks = rag_system.query(question)
        
        return jsonify({
            'answer': combined_text,
            'chunks': chunks
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port) 