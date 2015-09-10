(defproject irresponsible/tv100 "0.2.0"
  :description "Transform and Validate data structures"
  :url "https://github.com/jjl/tv100/"
  :license {:name "MIT License"
            :url "https://en.wikipedia.org/wiki/MIT_License"
            :distribution :repo}
  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-midje "3.1.3"]
            [codox "0.8.11"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src"]
  :test-paths ["t"]
  :clean-targets ^{:protect false} ["target"]
  :deploy-repositories [["releases" :clojars]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["doc"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "tv100-0.2.0.min.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]}
  :profiles {:dev {:dependencies [[midje "1.7.0"]]
                   :cljsbuild {:builds [{:source-paths ["src"]
                                         :compiler {:output-to "tv100.js"}}]}}})
