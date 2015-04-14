(defproject irresponsible/tv100 "0.1.0"
  :description "Transform and Validate data structures"
  :url "https://github.com/jjl/tv100/"
  :license {:name "MIT License"
            :url "https://en.wikipedia.org/wiki/MIT_License"
            :distribution :repo}
  :plugins [[lein-midje "3.1.3"]
            [codox "0.8.11"]]
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
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [collectible "0.1.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}})
