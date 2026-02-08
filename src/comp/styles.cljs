(ns comp.styles
  (:require [ti-yong.alpha.transformer :as t]
            [radiant.reagent :as r]
            [radiant.core :refer [css]]))

(defn dispatch-input-placeholder [sel k m]
  (let [s-m (->> m
                 (map (fn [[k v]]
                        (str
                         (str (name k) ":")
                         " "
                         (str v ";"))))
                 (apply str))
        s2 (str "." (:cls sel) "::" (name k) "{" s-m "}")]
    s2))

(defmethod css :style/-webkit-input-placeholder
  [sel k m]
  (dispatch-input-placeholder sel k m))

(defmethod css :style/-moz-placeholder
  [sel k m]
  (dispatch-input-placeholder sel k m))

(defmethod css :style/input-placeholder
  [sel k m]
  (dispatch-input-placeholder sel k m))

(def radiant
  (-> t/transformer
      (update :id conj ::radiant)
      (update :tf conj
              ::radiant
              (fn [{:as env :keys [props]}]
                (let [new-props (r/attrs->css props)]
                  (assoc env :props new-props))))))
