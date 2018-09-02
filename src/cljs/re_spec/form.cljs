(ns re-spec.form
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [cljs.spec.alpha :as s]
            [forms.core :as forms]
            [forms.controls :as form-controls]
            [re-spec.formatters :as formatters]
            [re-spec.model :as model]
            [re-spec.events :as events]
            [re-spec.utils :as utils]
            [re-spec.subs :as subs]))


;; inputs

(defn label-input [form-id]
  [:div.ui.form
   [form-controls/text-input
    form-id
    [:label]
    ::model/label
    "Label should be a string 1..64 chars long"
    {:field-classes ["required"]
     :label         "Label"}]])


(defn ranges-input [form-id]
  (let [ranges @(re-frame/subscribe [::forms/field-value form-id [:ranges]])
        {:keys [::forms/submitting?]} @(re-frame/subscribe [::forms/form-flags form-id])]

    [:div.ui.form
     [:div.required.field
      [:label "Ranges"]
      (->> (range (count ranges))
           (map (fn [i]
                  [:div.item
                   [:div.fields
                    [form-controls/number-input
                     form-id
                     [:ranges i :first]
                     ::model/first
                     "Invalid ASN"
                     {:field-classes ["six" "wide"]}]

                    [form-controls/number-input
                     form-id
                     [:ranges i :last]
                     ::model/first
                     "Invalid ASN"
                     {:field-classes ["six" "wide"]}]

                    [:div.four.wide.field
                     [:i.red.trash.link.icon
                      {:class    (when submitting? "disabled")
                       :on-click #(when-not submitting?
                                    (re-frame/dispatch [::forms/set-field-value form-id [:ranges]
                                                        (utils/vec-remove ranges i)]))}]]]]))

           (into [:div.items]))

      [:button.ui.mini.basic.teal.button
       {:class    (when submitting? "disabled")
        :on-click #(when-not submitting?
                     (re-frame/dispatch [::forms/set-field-value form-id [:ranges]
                                         (conj (or ranges []) {})]))}
       [:i.plus.circle.icon]
       " Add a range"]]]))


(defn types-radio-input [form-id]
  (let [field-path [:type]
        field-value @(re-frame/subscribe [::forms/field-value form-id field-path])
        {:keys [::forms/submitting?]} @(re-frame/subscribe [::forms/form-flags form-id])]
    (into [:div.ui.grouped.fields
           [:label "Type"]]
          (map (fn [{:keys [label value]}]
                 [:div.field
                  {:class (when submitting? "disabled")}
                  [:div.ui.radio.checkbox
                   [:input
                    {:type      "radio"
                     :checked   (= value field-value)
                     :on-change #(when (-> % .-target .-checked)
                                   (re-frame/dispatch [::forms/set-field-value form-id field-path value]))}]
                   [:label label]]])
               @(re-frame/subscribe [::subs/asn-types-api-response])))))


(defn types-select-input [form-id]
  (let [field-path [:type]
        field-value @(re-frame/subscribe [::forms/field-value form-id field-path])]
    (into [:select.ui.dropdown
           {:value     field-value
            :on-change #(re-frame/dispatch [::forms/set-field-value form-id field-path (-> % .-target .-value)])}]
          (map (fn [{:keys [label value]}]
                 [:option
                  {:value value}
                  label])
               @(re-frame/subscribe [::subs/asn-types-api-response])))))


;; form state handling

(defn form-state-wrapper [form-id]
  (reagent/create-class
    {:component-will-mount
     #(re-frame/dispatch [::forms/set-field-value form-id []
                          {:ranges       [{}]
                           :preallocated false}])

     :component-will-unmount
     #(re-frame/dispatch [::forms/clean-form-state form-id])

     :component-function
     (fn [form-id]
       (let [{:keys [::forms/initial-submit-dispatched?
                     ::forms/submitting?]} @(re-frame/subscribe [::forms/form-flags form-id])
             form-valid? (s/valid? ::model/create-payload @(re-frame/subscribe [::forms/field-value form-id []]))]

         [:div.ui.form
          [:div.ui.section
           [label-input form-id]
           [types-radio-input form-id]
           [ranges-input form-id]]

          [:div.ui.hidden.divider]

          [:div.ui.center.aligned.section
           [:button.ui.primary.button
            {:on-click (fn []
                         (when (not submitting?)
                           (when-not initial-submit-dispatched?
                             (re-frame/dispatch [::forms/set-flag-value form-id ::forms/initial-submit-dispatched? true]))
                           (when form-valid?
                             (re-frame/dispatch [::forms/set-flag-value form-id ::forms/submitting? true])
                             (re-frame/dispatch [::events/submit-form form-id]))))
             :class    (cond->> (list)
                                submitting? (cons "loading")
                                (and initial-submit-dispatched? (not form-valid?)) (cons "disabled")
                                true (clojure.string/join \space))}
            "Submit"]]]))}))


(defn main-panel []
  (let [form-id "form-state-wrapper"]
    [:div.ui.internally.celled.grid
     [:div.two.column.row
      [:div.column
       [:h4.ui.dividing.header "Rendered form"]
       [form-state-wrapper form-id]]
      [:div.column
       [:h4.ui.dividing.header "Source code"]
       [formatters/inspect @(re-frame/subscribe [::forms/field-value form-id []])]]]]))