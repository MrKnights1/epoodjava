#!/usr/bin/env fish
# Startup script for E-poe tellimuste süsteem

set PROJECT_DIR (dirname (status -f))

echo "🛍️  E-poe tellimuste süsteem"
echo "═══════════════════════════════════════════════════"
echo ""

# Check if JAR exists
if not test -f "$PROJECT_DIR/target/order-system-1.0-SNAPSHOT.jar"
    echo "⚠️  JAR fail puudub. Kompileerin projekti..."
    mvn clean package -DskipTests
    echo ""
end

# Run the application
echo "🚀 Käivitan rakendust..."
echo ""
java -jar "$PROJECT_DIR/target/order-system-1.0-SNAPSHOT.jar" $argv
