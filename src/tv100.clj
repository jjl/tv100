(ns tv100
  (:use [tv100.util :refer [safely safely-or]]
        [collectible :as c]))


;; # tv100
;;
;; A library for Transforming and Validating data
;; 
;; ## Introduction
;; 
;; I've been working on specifying processes in configuration data recently
;; (to make it machine manipulable) and I realised eventually what I needed
;; to make it easier was to combine transformation and validation and provide
;; combinators for these transform-validate functions (tv-fns)
;;
;; ## Diving in
;;
;; ```clojure
;; (ns myapp
;;  (:use [tv100])
;;
;; (defn double-int
;;  "An example tv-fn"
;;  [val]
;;  (if (integer? val)
;;   (* val 2)
;;   (throw (ex-info "Not an integer" {:got val})))))
;;
;; (def halve-int
;;  "An example of using v->tv, which takes a predicate and either returns
;;   the value or throws"
;;  (comp (v->tv "Not an integer? integer?) (partial * 2)))
;;
;; (def halve-int2
;;  "How i might actually write it, using the builtin tvint?"
;;  (comp (partial * 2) tvint))
;; ```
;;
;; ## Definitions
;; 
;; tv-fn
;; : a function which transforms a value and is expected to throw if the
;;   provided value is malformed
;;
;; t-fn
;; : a function which transforms a value
;;
;; v-fn:
;; : a (logical boolean) predicate. converts a value to a truthy/falsey
;;
;; ## The name
;;
;; This library combines two activities: validation and transformation (V and T).
;; Since two letters is too short and I don't like long names. Handily there's
;; prior art in this area:
;;
;; > The VT100 is a video terminal, introduced in August 1978 by Digital Equipment
;; > Corporation (DEC). It was one of the first terminals to support ANSI escape
;; > codes for cursor control and other tasks, and added a number of extended codes
;; > for special features like controlling the LED lamps on the keyboard.
;;
;; The underlying details of terminals are baroque and confusing and not really
;; extensible. This library is (hopefully) none of those things, so it amuses me
;; to name the library so.
;;
;; ## Function naming
;;
;; There are several different function name formats in use in this library:
;; - simple `[a-z+]` : simple functions. e.g. const
;; - tv predicates `tv[a-z]+\?`: simple predicates as tv-fns
;; - convertors `[a-z]+->[a-z]+` : takes a function, returns a function. e.g. t->tv
;; - combiners `tv-[a-z]+` : takes tv-fns, returns a tv-fn
;; - manufactured predicates `tv-[a-z]+\?` : takes args, returns a tv-fn

;; ## Basic functions

(def const
  "short for`constantly`"
  constantly)

(defn fail
  "[Internal]
   Throws an exception using msg as the message and value as exception info
   If value is not a map, it will be wrapped in one
   Args: [msg value]
   Returns: never
   Throws: ExceptionInfo"
  [msg value]
  (throw (ex-info msg (if (map? value) value {:form value}))))

;; ## Convertors

(defn t->tv
  "Turns a t-fn into a tv-fn. If t returns a truthy value,
   return it, else throw with exp-desc as the message
   Args: [exp-desc t-fn]
   Returns: tv-fn"
  [exp-desc t-f]
  (fn [val]
    (or (t-f val)
        (fail exp-desc val))))

(defn v->tv
  "Turns a v-fn a tv-fn. If it returns truthy, return
   the val unmodified, else throw with exp-desc as the message
   Args: [exp-desc pred]
   Returns: v-fn"
  [exp-desc v-f]
  (fn [val]
    (if (v-f val)
      val
      (fail exp-desc val))))

;; ## tv predicates

(def tvany?
  "A tv-fn that accepts and returns anything. equivalent to identity
   Args: [val]
   Return: val"
  identity)

(def tvtruthy?
  "A tv-fn that accepts and returns anything truthy.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not truthy"
  (v->tv "Expected truthy" identity))

(def tvfalsey?
  "A tv-fn that expects and returns anything falsey.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not falsey"
  (v->tv "Expected falsey" not))

(def tvnil?
  "A tv-fn that expects and returns nil.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not nil"
  (v->tv "Expected nil" nil?))

(def tvtrue?
  "A tv-fn that expects and returns true.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not true"
  (v->tv "Expected true" true?))

(def tvfalse?
  "A tv-fn that expects and returns false.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not false"
  (v->tv "Expected false" false?))

(def tvbool?
  "A tv-fn that expects and returns a boolean.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a bool"
  (v->tv "Expected boolean" #{true false}))

(def tvint?
  "A tv-fn that expects and returns an integer.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not an int"
  (v->tv "Expected int" integer?))

(def tvstr?
  "A tv-fn that expects and returns a string.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a string"
  (v->tv "Expected string" string?))

(def tvfloat?
  "A tv-fn that expects and returns a float.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a float"
  (v->tv "Expected float" float?))

(def tvkey?
  "A tv-fn that expects and returns a keyword.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a keyword"
  (v->tv "Expected keyword" keyword?))

(def tvsym?
  "A tv-fn that expects and returns a symbol.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a symbol"
  (v->tv "Expected symbol" symbol?))

(def tvfn?
  "A tv-fn that expects and returns a function.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a function"
  (v->tv "Expected function" fn?))

(def tvlist?
  "A tv-fn that expects and returns a list.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a list"
  (v->tv "Expected list" list?))

(def tvvec?
  "A tv-fn that expects and returns a vector.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a vector"
  (v->tv "Expected vector" vector?))

(def tvmap?
  "A tv-fn that expects and returns a map.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a map"
  (v->tv "Expected map" map?))

(def tvrecord?
  "A tv-fn that expects and returns a record.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not falsey"
  (v->tv "Expected record" record?))

(def tvclass?
  "A tv-fn that expects and returns a Class object.
   Args: [val]
   Return: val
   Throws: ExceptionInfo if not a Class object"
  (v->tv "Expected class" class?))

;; ## Combiners

(defn tv-update
  "Returns a tv-fn of the provided tv-fn over update-in
   in path by calling tv-fn on it. Path pieces are a list of keys as you'd
   provide to update-in (but without the vector)
   Args: [tv-fn & path-pieces]
   Returns: tv-fn"
  [tv-f & path-pieces]
  (fn [tv]
    (update-in tv path-pieces tv-f)))

(defn tv-or
  "Returns a tv-fn that returns the first successful tv-fn's return value
   Success is defined as not throwing. fail with exp-desc if none succeed
   Args: [exp-desc & tv-fns]
   Returns: tv-fn"
  [exp-desc & tv-fns]
  (fn [val]
    ;; This could be shorter, except nil must be a valid value
    (let [rs (->> tv-fns
                  (map #(safely-or ::fail (% val)))
                  (filter (partial not= ::fail)))]
      (if (seq rs)
        (first rs)
        (fail exp-desc val)))))

(defn tv-map
  "Returns a tv-fn that returns the value transformed by mapping tv-fn over it
o   Casts back to the original type of tv-fn
   Args: [tv-fn]
   Returns: tv-fn"
  [tv-fn]
  (fn [val]
    (into (empty val) (doall (map tv-fn val)))))

(defn tv-zip
  "Returns a tv-fn that zips its argument together by executing them with tv-fns
   Args: [& tv-fns]
   Returns: tv-fn"
  [& tv-fns]
  (fn [val]
    (let [rs (doall (map #(% %2) tv-fns val))]
      (into (empty val) rs))))

(defn tv-keys
  "Returns a tv-fn that applies tv-fn to all the keys in the provided map.
   Args: [tv-fn]
   Returns: tv-fn"
  [tv-fn]
  (fn [val]
    (->> val tvmap?
         (map (fn [[k v]] [(tv-fn k) v]))
         (into {}))))

(defn tv-vals
  "Returns a tv-fn that applies tv-fn to all the values in the provided map.
   Args: [tv-fn]
   Returns: tv-fn"
  [tv-fn]
  (fn [val]
    (->> val tvmap?
         (map (fn [[k v]] [k (tv-fn v)]))
         (into {}))))

;; ## Manufactured predicates

(defn tv=?
  "Returns a tv-fn that does an equality comparison with exp
   Args: [exp]
   Returns: tv-fn"
  [exp]
  (v->tv (str "Expected " exp) (partial = exp)))

(defn tv<=?
  "Returns a tv-fn that does a less-than-or-equals comparison with exp or low and high
   Args: [exp] [low high]
   Returns: tv-fn"
  ([exp]
     (v->tv (str "Expected " exp) (partial <= exp)))
  ([low high]
     (v->tv (str "Expected number between " low " and " high) #(<= low % high))))

(defn tv-count?
  "Returns a tv-fn that counts the provided collection and expects it to be either
   = count or low <= FOO <= high
   Args: [cnt] [low high]
   Returns: tv-fn"
  ([cnt]
     (v->tv (str "collection of length " cnt)
            (partial c/count= cnt)))
  ([low high]
     (v->tv (str "collection of length between " low " and " high " (inclusive)")
            (partial c/count<= low high))))

;; Coming next release: more functions.
;; (defn tv-isa?
;;   "Returns a tv-fn that does an isa? check against the given object
;;    Args: [parent]
;;    Returns: tv-fn"
;;   [parent]
;;   (t->tv #(isa? (if (class? %) % (class %)) parent)))

;; (defn tv-instance?
;;   "Returns a tv-fn that does an instance? check against the given class
;;    Args: [class]
;;    Returns: tv-fn"
;;   [c]
;;   (t->tv #(instance? % (if (class? c) c (class c)))))
