#!/bin/sh
until nc -z mmysql 3306; do
  echo "En attente de MySQL..."
  sleep 2
done
echo "MySQL est prêt !"
exec "$@"