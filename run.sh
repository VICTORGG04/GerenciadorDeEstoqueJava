#!/bin/bash

# 1. Limpeza
rm -rf target
mkdir -p target

# 2. Definição de Caminhos
PROJ_DIR=$(pwd)
GSON_JAR="$PROJ_DIR/gson-2.10.1.jar"
FX_LIB="/home/victor/IdeaProjects/javafx-sdk-26/lib"

# 3. COMPILAR
echo "Compilando..."
javac -d target \
      --module-path "$FX_LIB" \
      --add-modules javafx.controls,javafx.fxml \
      -cp ".:$GSON_JAR" \
      $(find src/main/java -name "*.java")

# 4. COPIAR RECURSOS (Isso faz o CSS funcionar)
echo "Sincronizando recursos..."
mkdir -p target/ui
cp src/main/resources/ui/style.css target/ui/ 2>/dev/null

# 5. EXECUTAR
if [ $? -eq 0 ]; then
    echo "Sucesso! Iniciando..."
    java --module-path "$FX_LIB" \
         --add-modules javafx.controls,javafx.fxml \
         -cp "target:.:$GSON_JAR" \
         ui.Main
else
    echo "Erro na compilação."
fi