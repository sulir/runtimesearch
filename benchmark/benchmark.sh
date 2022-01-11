#!/usr/bin/env bash

if [ ! -f "$1" ]; then
  echo "Usage: benchmark.sh dacapo_jar_path"
  exit 1
fi

dacapo_jar="$1"
dacapo_args=(--scratch-directory /tmp/dacapo-scratch)
IFS=' ' read -ra benchmarks <<< "$(java -jar "$dacapo_jar" -l 2>/dev/null)"

runtimesearch_jar=../dist/runtimesearch-agent.jar
searched_text=wHWGdeSPCC
LC_NUMERIC=C

run_dacapo() {
  java "${@:2}" -jar "$dacapo_jar" "${dacapo_args[@]}" "$1" 2>&1 \
    | grep -Eo 'PASSED in [0-9]+ msec' | cut -d" " -f3
}

run_instrumented() {
  run_dacapo "$1" -javaagent:$runtimesearch_jar
}

run_case_sensitive() {
  run_dacapo "$1" -javaagent:$runtimesearch_jar \
    -Druntimesearch.text=$searched_text -Druntimesearch.case=true
}

run_case_insensitive() {
  run_dacapo "$1" -javaagent:$runtimesearch_jar \
    -Druntimesearch.text=$searched_text
}

percentage() {
  echo "$1" "$2" | awk '{print ($2-$1)/$1*100}'
}

printf "benchmark\tplain\tinstrumented\tcase-sensitive\tcase-insensitive\n"

for benchmark in "${benchmarks[@]}"; do
  plain=$(run_dacapo "$benchmark")
  [ -z "$plain" ] && continue

  instrumented=$(run_instrumented "$benchmark")
  instrumented_percent=$(percentage "$plain" "$instrumented")

  case_sensitive=$(run_case_sensitive "$benchmark")
  case_sensitive_percent=$(percentage "$plain" "$case_sensitive")

  case_insensitive=$(run_case_insensitive "$benchmark")
  case_insensitive_percent=$(percentage "$plain" "$case_insensitive")

  printf "%-12s\t%5d\t%5d (%.1f%%)\t%6d (%.1f%%)\t%6d (%.1f%%)\n" \
    "$benchmark" "$plain" "$instrumented" "$instrumented_percent" \
    "$case_sensitive" "$case_sensitive_percent" \
    "$case_insensitive" "$case_insensitive_percent"
done
