; vim: syntax=clojure
(set-env!
 :project 'irresponsible/tv100
 :version "0.2.1"
 :source-paths #{"src"}
 :resource-paths #{"src" "resources"}
 :dependencies '[[org.clojure/clojure         "1.9.0-alpha15"          :scope "provided"]
                 [org.clojure/clojurescript   "1.9.518"        :scope "test"]
                 [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                 [adzerk/boot-test            "1.2.0"          :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.0"          :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as boot-test]
         '[crisptrutski.boot-cljs-test :as boot-cljs-test])

(task-options!
 pom {:project (get-env :project)
      :version (get-env :version)
      :description "Utilities for transforming and validating data"
      :url "https://github.com/irresponsible/tv100"
      :scm {:url "https://github.com/irresponsible/tv100"}
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
  (boot-test/test))

(deftask test-cljs []
  (testing)
  (boot-cljs-test/test-cljs))

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

; This is a stub target intended to cause dependency propagation
; to trigger without actually doing any work
(deftask travis-installdeps []
  (testing) identity)

(deftask jitpak-deploy []
  (task-options! pom {
    :project (symbol (System/getenv "ARTIFACT"))
  })
  (comp
    (pom)
    (jar)
    (target)      ; Must install to build dir
    (install)     ; And to .m2 https://jitpack.io/docs/BUILDING/#build-customization
  )
)
