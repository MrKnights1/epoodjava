#!/bin/bash
# Startup script for E-poe tellimuste süsteem

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🛍️  E-poe tellimuste süsteem"
echo "═══════════════════════════════════════════════════"
echo ""

# Check if JAR exists
if [ ! -f "$PROJECT_DIR/target/order-system-1.0-SNAPSHOT.jar" ]; then
    echo "⚠️  JAR fail puudub. Kompileerin projekti..."
    mvn clean package -DskipTests
    echo ""
fi

# Run the application
echo "🚀 Käivitan rakendust..."
echo ""
java -jar "$PROJECT_DIR/target/order-system-1.0-SNAPSHOT.jar" "$@"
