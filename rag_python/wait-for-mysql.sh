#!/bin/sh
# Remplacer nc par une commande équivalente
until mysqladmin ping -h"mmysql" -P"3306" -u"ayoub" -p"ayoub" --silent; do
  echo "En attente de MySQL..."
  sleep 2
done
echo "MySQL est prêt !"
exec "$@"