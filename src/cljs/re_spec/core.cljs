(ns re-spec.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-spec.views :as views]
            [re-spec.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))



(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))



(defn ^:export init []
  (dev-setup)
  (mount-root))
