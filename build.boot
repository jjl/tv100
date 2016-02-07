(set-env!
 :project 'irresponsible/tv100
 :version "0.3.0-SNAPSHOT"
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0"                  :scope "provided"]
                 [org.clojure/clojurescript "1.7.228"          :scope "test"]
                 [adzerk/boot-cljs "1.7.228-1"                 :scope "test"]
                 [adzerk/boot-test "1.1.0"                     :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as t]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
 pom {:project (get-env :project)
      :version (get-env :version)
      :description "Utilities for transforming and validating data"
      :url "https://github.com/irresponsible/tv100"
      :scm {:url "https://github.com/irresponsible/tv100.git"}
      :license {"MIT" "https://en.wikipedia.org/MIT_License"}}
 test-cljs {:js-env :node}
 target  {:dir #{"target"}})

(deftask clj-tests []
  (set-env! :source-paths #(conj % "test"))
  (comp (speak) (t/test)))

(deftask cljs-tests []
  (set-env! :source-paths #(conj % "test"))
  (comp (speak) (test-cljs)))

(deftask tests []
  (set-env! :source-paths #(conj % "test"))
  (comp (speak) (t/test) (test-cljs)))

(deftask autotest-clj []
  (set-env! :source-paths #(conj % "test"))
  (comp (watch) (speak) (t/test)))

(deftask autotest-cljs []
  (set-env! :source-paths #(conj % "test"))
  (comp (watch) (speak) (test-cljs)))

(deftask autotest []
  (comp (watch) (tests)))

(deftask make-release-jar []
  (comp (pom) (uber) (jar)))
