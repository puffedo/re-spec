(ns re-spec.views
  (:require [re-spec.form]))


(defn main-panel []
    [:div.ui.container
     {:style {:margin-top "5em"}}
     [:h1.ui.header "Form with re-frame and clojure.spec"]
     [:div.ui.grid
      [:div.two.column.row
       [:div.twelve.wide.column
        [:div.section
         [re-spec.form/main-panel]]]]]])
