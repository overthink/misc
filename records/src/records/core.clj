(ns records.core
  (:import
    java.sql.Timestamp))

(defrecord R1 [ts name age])

(defrecord R2 [^java.sql.Timestamp ts
               ^String  bar
               ^Integer baz])

