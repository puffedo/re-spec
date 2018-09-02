(ns re-spec.events
  (:require [re-frame.core :as re-frame]
            [forms.core :as forms]))



(defn form-valid? [form-value]
  (not= (:label form-value) "asdf"))

(re-frame/reg-event-fx
  ::simulate-api-server-response
  (fn [{:keys [db]} [_ form-id]]
    {:db       (let [form-value (get-in db (conj forms/value-db-path form-id))]
                 (assoc-in db [::api-response form-id]
                           (if (form-valid? form-value)
                             201
                             422)))
     :dispatch [::forms/set-flag-value form-id ::forms/submitting? false]}))


(re-frame/reg-sub
  ::api-server-response
  (fn [db [_ form-id]]
    (get-in db [::api-response form-id])))


(re-frame/reg-event-fx
  ::submit-form
  (fn [_ [_ form-id]]
    {:dispatch-later [{:ms       5000
                       :dispatch [::simulate-api-server-response form-id]}]}))