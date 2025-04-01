@echo off
call conda activate rag_env
cd %~dp0
python app/api.py 