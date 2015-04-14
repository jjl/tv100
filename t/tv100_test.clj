(ns tv100-test
  (:use [midje.sweet]
        [tv100]
        [tv100.util])
  (:import [clojure.lang ExceptionInfo]))

(def twice (partial * 2))
(def never (constantly nil))

(facts :simple
  (= const constantly)
  (fact "fail throws an exception"
    (fail "foo" {}) => (throws ExceptionInfo "foo")))

(facts "simple checkers"
  (let [ts [1 :a 'a "a" 1.0 nil true false]]
    (letfn [(tf [f exp]
              (tfe f exp ts))
            (tfe [f exp ts]
              (dorun (map #(safely-or ::fail f %) ts)) => exp)]
      (fact :tvany?
        (tf tvany? ts))
      (fact :tvtruthy?
        (tf tvtruthy? [1 :a 'a "a" 1.0 ::fail true ::fail])
        (tvtruthy? nil) => (throws ExceptionInfo "Expected truthy"))
      (fact :tvfalsey?
        (tf tvfalsey? [::fail ::fail ::fail ::fail ::fail nil ::fail false])
        (tvfalsey? 1)   => (throws ExceptionInfo "Expected falsey"))
      (fact :tvnil?
        (tf tvnil? [::fail ::fail ::fail ::fail ::fail nil ::fail ::fail])
        (tvnil? 1)   => (throws ExceptionInfo "Expected nil"))
      (fact :tvtrue?
        (tf tvtrue? [::fail ::fail ::fail ::fail ::fail ::fail true ::fail])
        (tvtrue? nil)  => (throws ExceptionInfo "Expected true"))
      (fact :tvfalse?
        (tf tvfalse? [::fail ::fail ::fail ::fail ::fail ::fail ::fail false])
        (tvfalse? nil)   => (throws ExceptionInfo "Expected false"))
      (fact :tvbool?
        (tf tvbool? [::fail ::fail ::fail ::fail ::fail ::fail true false])
        (tvbool? nil) => (throws ExceptionInfo "Expected boolean"))
      (fact :tvint?
        (tf tvint? [1 ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tvint? nil) => (throws ExceptionInfo "Expected int"))
      (fact :tvstr?
        (tf tvstr? [::fail ::fail ::fail "a" ::fail ::fail ::fail ::fail])
        (tvstr? nil) => (throws ExceptionInfo "Expected string"))
      (fact :tvfloat?
        (tf tvfloat? [::fail ::fail ::fail ::fail 1.0 ::fail ::fail ::fail])
        (tvfloat? nil) => (throws ExceptionInfo "Expected float"))
      (fact :tvkey?
        (tf tvkey? [::fail :a ::fail ::fail ::fail ::fail ::fail ::fail])
        (tvkey? nil)  => (throws ExceptionInfo "Expected keyword"))
      (fact :tvsym?
        (tf tvsym? [::fail ::fail 'a ::fail ::fail ::fail ::fail ::fail])
        (tvsym? nil) => (throws ExceptionInfo "Expected symbol"))
      (fact :tvfn?
        (tf tvfn? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tvfn? nil) => (throws ExceptionInfo "Expected function")
        (tvfn? identity) => fn?)
      (fact :tvlist?
        (tf tvlist? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe tvlist? [{:a :b} [1] '(1)] [::fail ::fail '(1) ::fail])
        (tvlist? nil)  => (throws ExceptionInfo "Expected list"))
      (fact :tvvec?
        (tf tvvec? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe tvvec? [{:a :b} [1] '(1)] [::fail [1] ::fail])
        (tvvec? nil)  => (throws ExceptionInfo "Expected vector"))
      (fact :tvmap?
        (tf tvmap? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe tvmap? [{:a :b} [1] '(1)] [{:a :b} ::fail  ::fail])
        (tvmap? nil)     => (throws ExceptionInfo "Expected map"))
      (fact :tvrecord?
        (defrecord foo [])
        (tf tvrecord? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tfe tvrecord? [{:a :b} [1] '(1) (map->foo {})] [::fail ::fail  ::fail {}])
        (tvrecord? nil)     => (throws ExceptionInfo "Expected record"))
      (fact :tvclass?
        (tf tvclass? [::fail ::fail ::fail ::fail ::fail ::fail ::fail ::fail])
        (tvclass? ExceptionInfo) => ExceptionInfo
        (tvclass? nil) => (throws ExceptionInfo "Expected class")))))


(facts :convertors
  (facts :t->tv
    (fact "returns a function"
      (t->tv "twice" twice) => fn?)
    (fact "transforms"
      ((t->tv "twice" twice) 123) => 246)
    (fact "validates"
      ((t->tv "never" never) 123) => (throws ExceptionInfo "never")))
  (facts :v->tv
    (fact "returns a function"
      (v->tv "nilly" nil?) => fn?)
    (fact "validates"
      ((v->tv "nilly" nil?) nil) => nil
      ((v->tv "int" integer?) 123) => 123
      ((v->tv "nilly" nil?) 123) => (throws ExceptionInfo "nilly"))))

(facts :combiners
  (fact :tv-update
    ((tv-update twice :foo) {:foo 2}) => {:foo 4})
  (fact :tv-or
    (let [f (tv-or "meh" (v->tv "nilly" nil?) (v->tv "true" true?))]
      f => fn?
      (f nil) => nil
      (f true) => true
      (f 123) => (throws ExceptionInfo "meh")))
  (facts :tv-map
    (let [f (tv-map tvnil?)]
      (fact "returns a function"
        f => fn?)
      (fact "transforms"
        (f []) => []
        (f [nil nil]) => [nil nil])
      (fact "validates"
        (f [nil 1]) => (throws ExceptionInfo "Expected nil"))))
  (fact :tv-zip
    (let [f (tv-zip twice never)]
      (fact "returns function"
        f => fn?)
      (fact "transforms"
        (f [2 3]) => [4 nil]))
    (fact "odd arities"
      ((tv-zip tvnil?) [nil nil]) => [nil]
      ((tv-zip tvnil? tvnil?) [nil]) => [nil])
    (fact "validates"
      ((tv-zip tvnil?) [1]) => (throws ExceptionInfo "Expected nil")))
  (fact :tv-keys
    (let [f (tv-keys twice)]
      (fact "returns function"
        f => fn?)
      (fact "transforms"
        (f {2 4}) => {4 4})))
  (fact :tv-vals
    (let [f (tv-vals twice)]
      (fact "returns function"
        f => fn?)
      (fact "transforms"
        (f {2 4}) => {2 8})))
  (fact :tv-count?
    (let [f1 (tv-count? 1)
          f2 (tv-count? 1 2)]
      (fact "returns a function"
        f1 => fn?
        f2 => fn?)
      (fact "checks equality"
        (f1 [:a]) => [:a]
        (f1 []) => (throws ExceptionInfo "collection of length 1")
        (f1 [:a :b]) => (throws ExceptionInfo "collection of length 1"))
      (fact "checks range"
        (f2 [:a]) => [:a]
        (f2 [])
        => (throws ExceptionInfo "collection of length between 1 and 2 (inclusive)")
        (f2 [:a :b :c]) =>
        (throws ExceptionInfo "collection of length between 1 and 2 (inclusive)")))))
  ;; TODO: coming soon
  ;; (fact :tv-isa?)
  ;; (fact :tv-instance?))
