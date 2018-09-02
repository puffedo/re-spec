(ns re-spec.formatters
  (:require [cljs.pprint :as pp]))


(defn inspect [data & [pprint?]]
  [:pre
   [:code
    (if pprint?
      (with-out-str (pp/pprint data))
      (.stringify js/JSON (clj->js data) nil 2))]])
