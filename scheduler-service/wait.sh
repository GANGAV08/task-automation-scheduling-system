#!/bin/sh
set -e

# Wait for MySQL
echo "Waiting for scheduler-db..."
until mysql -h scheduler-db -uroot -proot -e "SELECT 1;" >/dev/null 2>&1; do
  sleep 2
done

# Wait for executor-service
echo "Waiting for executor-service..."
until curl -f http://executor-service:8090/actuator/health >/dev/null 2>&1; do
  sleep 2
done

# Start the scheduler-service
echo "Starting scheduler-service..."
exec java -jar /app/app.jar