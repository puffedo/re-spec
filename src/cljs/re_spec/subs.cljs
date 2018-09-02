(ns re-spec.subs
  (:require [re-frame.core :as re-frame]
            [re-spec.backend-sim :as back-end]))


(re-frame/reg-sub
  ::asn-types-api-response
  (constantly back-end/asn-types))