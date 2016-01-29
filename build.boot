(def project 'irresponsible/tv100)
(def version "0.2.0")
(def description "Utilities for transforming and validating data")

(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.7.0"          :scope "provided"]
                  [org.clojure/clojurescript "1.7.228"  :scope "test"]
                  [midje "1.8.3"                        :scope "test"]
                  [adzerk/boot-cljs "1.7.228-1"         :scope "test"]
                  [boot-deps "0.1.6"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[midje.repl :as m]
         '[boot-deps :refer [ancient latest]])
    
(task-options!
  pom {:project project
       :version version
       :license {"MIT" "https://en.wikipedia.org/wiki/MIT_License"}})
;  midje {:test-paths #{"t"}})

(deftask uberjar []
  (comp (uber) (jar)))

(deftask local []
  (comp (jar) (install)))

(deftask testing []
  (set-env! :source-paths #(conj % "t"))
  identity)

(deftask run-midje []
  (m/load-facts))

(deftask midje []
  (comp (testing) (run-midje)))
  
(deftask dev []
  identity)

(deftask release []
  identity)
