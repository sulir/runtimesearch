rootProject.name = "runtimesearch"
include("shared", "agent", "plugin")
project(":shared").name = "runtimesearch-shared"
project(":agent").name = "runtimesearch-agent"
project(":plugin").name = "runtimesearch-plugin"