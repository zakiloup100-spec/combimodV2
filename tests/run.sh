#!/bin/sh
# Lance les tests de la logique pure (aucune classe Minecraft requise).
# Usage : sh tests/run.sh   (depuis la racine du projet, JDK 21)
set -e
OUT=$(mktemp -d)
if command -v javac >/dev/null 2>&1; then
    JAVAC="javac"
else
    JAVAC="java -m jdk.compiler/com.sun.tools.javac.Main"
fi
$JAVAC -encoding UTF-8 -d "$OUT" src/main/java/com/example/combimod/core/*.java tests/test/*.java
java -Dfile.encoding=UTF-8 -cp "$OUT" test.CoreTest
java -Dfile.encoding=UTF-8 -cp "$OUT" test.ContentTest
java -Dfile.encoding=UTF-8 -cp "$OUT" test.GuildTest
rm -rf "$OUT"
