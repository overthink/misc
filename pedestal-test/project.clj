(defproject pedestal-test "0.0.1-SNAPSHOT"
  :description "try out a pedestal service"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.pedestal/pedestal.service "0.1.9"]
                 [io.pedestal/pedestal.jetty "0.1.9"]

                 ;; auto-reload changes
                 [ns-tracker "0.2.1"]

                 ;; Logging
                 [ch.qos.logback/logback-classic "1.0.7" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.2"]
                 [org.slf4j/jcl-over-slf4j "1.7.2"]
                 [org.slf4j/log4j-over-slf4j "1.7.2"]]
  :profiles {:dev {:source-paths ["dev"]}}
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :aliases {"run-dev" ["trampoline" "run" "-m" "dev"]}
  :main ^{:skip-aot true} pedestal-test.server)

