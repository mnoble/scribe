(defproject scribe "0.0.1"
  :description "Distributed log collector"
  :url "http://github.com/mnoble/scribe"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [io.pedestal/pedestal.service "0.2.2"]
                 [io.pedestal/pedestal.service-tools "0.2.2"]
                 [io.pedestal/pedestal.jetty "0.2.2"]
                 [org.clojure/data.json "0.2.3"]
                 [com.taoensso/carmine "2.4.0"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :aliases {"run-dev" ["trampoline" "run" "-m" "scribe.server/run-dev"]}
  :repl-options  {:init-ns user
                  :init (try
                          (use 'io.pedestal.service-tools.dev)
                          (require 'scribe.service)
                          ;; Nasty trick to get around being unable to reference non-clojure.core symbols in :init
                          (eval '(init scribe.service/service #'scribe.service/routes))
                          (catch Throwable t
                            (println "ERROR: There was a problem loading io.pedestal.service-tools.dev")
                            (clojure.stacktrace/print-stack-trace t)
                            (println)))
                  :welcome (println "Welcome to pedestal-service! Run (tools-help) to see a list of useful functions.")}
  :main ^{:skip-aot true} scribe.server)
