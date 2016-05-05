(set-env!
 :project 'irresponsible/tv100
 :version "0.2.0"
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure         "1.8.0"          :scope "provided"]
                 [org.clojure/clojurescript   "1.8.40"         :scope "test"]
                 [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                 [adzerk/boot-test            "1.1.0"          :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as boot-test]
         '[crisptrutski.boot-cljs-test :as boot-cljs-test])

(task-options!
 pom {:project (get-env :project)
      :version (get-env :version)
      :description "Utilities for transforming and validating data"
      :url "https://github.com/irresponsible/tv100"
      :scm {:url "https://github.com/irresponsible/tv100.git"}
      :license {"MIT" "https://en.wikipedia.org/MIT_License"}}
 ;; marginalia {:dir "doc"
 ;;             :file "index.html"}
  push {:tag            true
        :ensure-branch  "master"
        :ensure-release true
        :ensure-clean   true
        :gpg-sign       true
        :repo-map [["clojars" {:url "https://clojars.org/repo/"}]]}
 boot-cljs-test/test-cljs {:js-env :node}
 target  {:dir #{"target"}})

(deftask testing []
  (set-env! :resource-paths #(conj % "test"))
  (set-env! :source-paths   #(conj % "test")))
  
(deftask test-clj []
  (testing)
  (comp (boot-test/test)))

(deftask test-cljs []
  (testing)
  (comp (boot-cljs-test/test-cljs)))

(deftask test []
  (testing)
  (comp (boot-test/test) (boot-cljs-test/test-cljs)))

(deftask autotest-clj []
  (testing)
  (comp (watch) (boot-test/test)))

(deftask autotest-cljs []
  (testing)
  (comp (watch) (boot-cljs-test/test-cljs)))

(deftask autotest []
  (comp (watch) (test)))

(deftask make-jar []
  (comp (target) (pom) (jar)))

(deftask travis []
  (testing)
  (comp (boot-test/test) (boot-cljs-test/test-cljs)))
