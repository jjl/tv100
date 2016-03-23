(ns irresponsible.tv100-test
  #?@(:clj [(:require [clojure.test :refer [deftest is]]
                      [irresponsible.tv100 :as t])
            (:import [clojure.lang ExceptionInfo])]
      :cljs [(:require [irresponsible.tv100 :as t]
                       [cljs.test :refer [do-report]])
             (:require-macros [cljs.test :refer [deftest is]])]))
(def twice (partial * 2))
(def never (constantly nil))

(deftest simple
  (is (= t/const constantly))
  (is (= (try
           (t/fail "foo" {})
           (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
             #?(:clj (is (= (.getMessage e) "foo")))
             ::caught)) ::caught)))

(let [ts [1 :a 'a "a" 1.0 nil true false]]
  (letfn [(tf [f exp]
            (tfe f exp ts))
          (tfe [f exp ts]
            (is (= exp (mapv #(try (f %)
                                   (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                                     ::fail)) ts))))]
    (deftest tvany?
      (tf t/tvany? ts))
    (deftest tvtruthy?
      (tf t/tvtruthy? [1 :a 'a "a" 1.0 ::fail true ::fail])
      (is (= (try (t/tvtruthy? nil)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "Expected truthy")))
                    ::caught)) ::caught)))
`    (deftest tvfalsey?
      (tf t/tvfalsey? [::fail ::fail ::fail ::fail ::fail nil ::fail false])
      (is (= (try (t/tvfalsey? 1)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "Expected falsey")))
                    ::caught)) ::caught)))
    (deftest tvnil?
      (tf t/tvnil? [::fail ::fail ::fail ::fail ::fail nil ::fail ::fail])
      (is (= (try (t/tvnil? 1)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "Expected nil")))
                    ::caught)) ::caught)))
    (deftest tvtrue?
      (tf t/tvtrue? [::fail ::fail ::fail ::fail ::fail ::fail true ::fail])
      (is (= (try (t/tvtrue? nil)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "Expected true")))
                    ::caught)) ::caught)))
    (deftest tvfalse?
      (tf t/tvfalse? [::fail ::fail ::fail ::fail ::fail ::fail ::fail false])
      (is (= (try (t/tvfalse? nil)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "Expected false")))
                    ::caught)) ::caught)))
      (deftest tvbool?
        (tf t/tvbool? [::fail ::fail ::fail ::fail ::fail ::fail true false])
        (is (= (try (t/tvbool? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected boolean")))
                      ::caught)) ::caught)))
#?(:clj (deftest tvint?
        (tf t/tvint? [1 ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (is (= (try (t/tvint? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected int")))
                      ::caught)) ::caught))))
      (deftest tvstr?
        (tf t/tvstr? [::fail ::fail ::fail "a" ::fail ::fail ::fail ::fail])
        (is (= (try (t/tvstr? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected string")))
                      ::caught)) ::caught)))
      (deftest tvfloat?
        (tf t/tvfloat? #?(:clj [::fail ::fail ::fail ::fail 1.0 ::fail ::fail ::fail]
                          :cljs [1 ::fail ::fail ::fail 1.0 ::fail ::fail ::fail]))
        (is (= (try (t/tvfloat? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected float")))
                      ::caught)) ::caught)))
      (deftest tvkey?
        (tf t/tvkey? [::fail :a ::fail ::fail ::fail ::fail ::fail ::fail])
        (is (= (try (t/tvkey? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected keyword")))
                      ::caught)) ::caught)))
      (deftest tvsym?
        (tf t/tvsym? [::fail ::fail 'a ::fail ::fail ::fail ::fail ::fail])
        (is (= (try (t/tvsym? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected symbol")))
                      ::caught)) ::caught)))
      (deftest tvfn?
        (tf t/tvfn? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (is (fn? (t/tvfn? identity)))
        (is (= (try (t/tvfn? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected function")))
                      ::caught)) ::caught)))
      (deftest tvlist?
        (tf t/tvlist? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe t/tvlist? [::fail ::fail '(1)] [{:a :b} [1] '(1)])
        (is (= (try (t/tvlist? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected list")))
                      ::caught)) ::caught)))
      (deftest tvvec?
        (tf t/tvvec? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe t/tvvec? [::fail [1] ::fail] [{:a :b} [1] '(1)])
        (is (= (try (t/tvvec? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected vector")))
                      ::caught)) ::caught)))
      (deftest tvmap?
        (tf t/tvmap? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe t/tvmap? [{:a :b} ::fail  ::fail] [{:a :b} [1] '(1)])
        (is (= (try (t/tvmap? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected map")))
                      ::caught)) ::caught)))
      (deftest tvrecord?
        (defrecord foo [])
        (tf t/tvrecord? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe t/tvrecord? [::fail ::fail  ::fail (map->foo {})] [{:a :b} [1] '(1) (map->foo {})])
        (is (= (try (t/tvrecord? nil)
                    (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                      #?(:clj (is (= (.getMessage e) "Expected record")))
                      ::caught)) ::caught)))
      #?(:clj
         (deftest tvclass?
           (tf t/tvclass? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
           (is (= #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) (t/tvclass? #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo))))
           (is (= (try (t/tvclass? nil)
                       (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                         #?(:clj (is (= (.getMessage e) "Expected class")))
                      ::caught)) ::caught))))
      #?(:clj (deftest tv-isa?
                (is (= 123 ((t/tv-isa? #?(:clj java.lang.Long :cljs js/Number)) 123)))
                (is (= 123 ((t/tv-isa? 456) 123)))))
      #?(:clj (deftest tv-instance?
                (is (= 123 ((t/tv-instance? #?(:clj java.lang.Long :cljs js/Number)) 123)))
                (is (= 123 ((t/tv-instance? 456) 123)))))))

;; convertors
(deftest t->tv
  (is (fn? (t/t->tv "twice" twice)))
  (is (= ((t/t->tv "twice" twice) 123) 246))
  (is (= (try
           ((t/t->tv "never" never) 123)
           (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
             #?(:clj (is (= (.getMessage e) "never")))
             ::caught)) ::caught)))
(deftest pred->tv
  (let [nilly (t/pred->tv "nilly" nil?)
        keyy (t/pred->tv "keyy" keyword?)]
    (is (fn? nilly))
    (is (nil? (nilly nil)))
    (is (= (keyy :foo) :foo))
    (is (= (try
             (nilly :foo)
             (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
               #?(:clj (= (.getMessage e) "nilly"))
               ::caught)) ::caught))))

; Use cases:
; - Actually filter the results
; - Filter them for purposes of applying extra checks
; 
; (tv-filter tv-fn)
; (tv-with-matching tv-fn)

(deftest tv-swap
  (is (fn? (t/tv-swap t/tvkey?)))
  (is (= :foo ((t/tv-swap t/tvkey?) :foo))))

(deftest tv-update
  (is (= {:foo 4} ((t/tv-update twice :foo) {:foo 2}))))

(deftest tv-or
  (let [f (t/tv-or "meh" (t/pred->tv "nilly" nil?) (t/pred->tv "true" true?))]
      (is (fn? f))
      (is (nil? (f nil)))
      (is (= true (f true)))
      (is (= (try (f 123)
                  (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                    #?(:clj (is (= (.getMessage e) "meh")))
                    ::caught)) ::caught))))
(deftest tv-map
  (let [f (t/tv-map t/tvnil?)]
    (is (fn? f))
    (is (= [] (f [])))
    (is (= [nil nil] (f [nil nil])))
    (is (= (try (f [nil 1])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "Expected nil")))
                  ::caught)) ::caught))))
(deftest tv-zip
  (let [f (t/tv-zip twice never)]
    (is (fn? f))
    (is (= [4 nil] (f [2 3])))
    (is (= [nil] ((t/tv-zip t/tvnil?) [nil nil])))
    (is (= [nil] ((t/tv-zip t/tvnil? t/tvnil?) [nil])))
    (is (= (try ((t/tv-zip t/tvnil?) [1])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "Expected nil")))
                  ::caught)) ::caught))))
(deftest tv-keys
    (let [f (t/tv-keys twice)]
      (is (fn? f))
      (is (= {4 4} (f {2 4})))))
(deftest tv-vals
  (let [f (t/tv-vals twice)]
    (is (fn? f))
    (is (= {2 8} (f {2 4})))))
(deftest tv-slice
  (let [f (t/tv-slice 1 2 (partial map (partial * 2)))
        f2 (t/tv-slice -1 2 (partial map (partial * 2)))
        f3 (t/tv-slice 0 2 (partial map (partial * 2)))]
    (is (fn? f))
    (is (= [1 4 6 4 5] (f [1 2 3 4 5])))
    (is (= [2 4 3 4 5] (f2 [1 2 3 4 5])))
    (is (= [2 4 3 4 5] (f3 [1 2 3 4 5])))))
(deftest tv-count?
  (let [f1 (t/tv-count? 1)
        f2 (t/tv-count? 1 2)]
    (is (and (fn? f1) (fn? f2)))
    (is (= [:a] (f1 [:a])))
    (is (= (try (f1 [])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "collection of length 1")))
                  ::caught)) ::caught))
    (is (= (try (f1 [:a :b])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "collection of length 1")))
                  ::caught)) ::caught))
    (is (= [:a] (f2 [:a])))
    (is (= (try (f2 [])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "collection of length between 1 and 2 (inclusive)")))
                  ::caught)) ::caught))
    (is (= (try (f2 [:a :b :c])
                (catch #?(:clj ExceptionInfo :cljs cljs.core.ExceptionInfo) e
                  #?(:clj (is (= (.getMessage e) "collection of length between 1 and 2 (inclusive)")))
                  ::caught)) ::caught))))
