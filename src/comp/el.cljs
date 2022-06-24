(ns comp.el
  (:require
   [reagent-mui.icons.keyboard-arrow-down :refer [keyboard-arrow-down]]
   [reagent-mui.material.box :as mui-box]
   [reagent-mui.material.divider :as mui-divider]
   [reagent-mui.material.grid :as mui-grid]
   [reagent-mui.material.list :as mui-list]
   [reagent-mui.material.list-item :as mui-list-item]
   [reagent-mui.material.paper :as mui-paper]
  ;;  [reagent-mui.material.text-field :as mui-text-field]
   [af.fect :as af]
   [comp.click :as c]
   [comp.styles :as s]
   [comp.props :as p]))

(defn form-1-or-2 [{:as env :keys [pre-state props args]} & _effect-args]
  (let [props (or props {})
        component (:comp props :<>)
        [props args] (if (map? (first args))
                       [(merge props (first args)) (rest args)]
                       [props args])]
    (into [component (dissoc props :comp)] args)))

(def el
  (af/fect
   {:as ::el :with [s/radiant c/click p/props p/void]
    :op-env form-1-or-2}))

(def div
  (el
   {:as ::div
    :props {:comp :div}}))

;; (def input
;;   (el
;;    {:as ::input
;;     :props {:comp mui-text-field/text-field}}))

(def raw-input
  (el
   {:as ::input
    :props {:comp :input}}))

(def box
  (el
   {:as ::box
    :props {:comp mui-box/box}}))

(def paper
  (el
   {:as ::paper
    :props {:comp mui-paper/paper}}))

(def list-items
  (el
   {:as ::list-items
    :props {:disable-padding true
            :comp mui-list/list}}))

(def list-item
  (el
   {:as ::list-item
    :props {:disable-padding true
            :comp mui-list-item/list-item}}))

(def divider
  (el
   {:as ::divider
    :props {:comp mui-divider/divider}}))

(def grid
  (el
   {:as ::grid
    :props {:comp mui-grid/grid}}))

(def container
  (grid
   {:as ::container
    :props {:container true}}))

(def item
  (grid
   {:as ::item
    :props {:item true}}))

(def arrow-down
  (el
   {:as ::keyboard-arrow-down
    :props {:comp keyboard-arrow-down}}))

(def label
  (el
   {:as ::label
    :props {:comp :label}}))

(def a
  (el
   {:as ::a
    :props {:comp :a}}))
