(ns tv100.util)

(defmacro safely [& exprs]
  `(try ~@exprs
        (catch Exception e#)))

(defmacro safely-or [or-v & exprs]
  `(try ~@exprs
        (catch Exception e# ~or-v)))
