(ns comp.el
  (:require
   [reagent-mui.icons.keyboard-arrow-down :refer [keyboard-arrow-down]]
   [reagent-mui.material.css-baseline :as Css-baseline]
   [reagent-mui.material.app-bar :as App-bar]
   [reagent-mui.material.toolbar :as Toolbar]
   [reagent-mui.styles :as styles]
   [reagent-mui.material.box :as mui-box]
   [reagent-mui.icons.home :as mui-home]
   [reagent-mui.material.divider :as mui-divider]
   [reagent-mui.material.stack :as mui-stack]
   [reagent-mui.material.grid :as mui-grid]
   [reagent-mui.icons.account-circle :as mui-account-circle]
   [reagent-mui.icons.menu :as mui-menu-icon]
   [reagent-mui.icons.notifications :as mui-notifications]
   [reagent-mui.icons.exit-to-app :as mui-exit-to-app]
   [reagent-mui.icons.lock :as mui-lock]
   [reagent-mui.material.form-control-label :as mui-form-control-label]
   [reagent-mui.material.checkbox :as mui-checkbox]
   [reagent-mui.material.container :as mui-container]
   [reagent-mui.material.table :as mui-table]
   [reagent-mui.material.table-head :as mui-table-head]
   [reagent-mui.material.table-body :as mui-table-body]
   [reagent-mui.material.table-row :as mui-table-row]
   [reagent-mui.material.table-cell :as mui-table-cell]
   [reagent-mui.material.link :as mui-link]
   [reagent-mui.material.avatar :as mui-avatar]
   [reagent-mui.material.button :as mui-button]
   [reagent-mui.material.icon-button :as mui-icon-button]
   [reagent-mui.material.menu :as mui-menu]
   [reagent-mui.material.menu-item :as mui-menu-item]
   [reagent-mui.material.typography :as mui-typography]
   [reagent-mui.material.badge :as mui-badge]
   [reagent-mui.material.drawer :as mui-drawer]
   [reagent-mui.material.list-item-icon :as mui-list-item-icon]
   [reagent-mui.material.list-item-text :as mui-list-item-text]
   [reagent-mui.material.switch-component :as mui-switch]
   [reagent-mui.material.list :as mui-list]
   [reagent-mui.material.list-item :as mui-list-item]
   [reagent-mui.material.paper :as mui-paper]
   [reagent-mui.material.text-field :as mui-text-field]
   [ti-yong.alpha.transformer :as t]
   [ti-yong.alpha.util :as u]
   [comp.click :as c]
   [comp.styles :as s]
   [comp.props :as p]))

(def theme-provider
  styles/theme-provider)

(def create-theme
  styles/create-theme)

(def styled
  styles/styled)

(defn derive
  "Derive a new transformer from a parent, merging a config map.
   Config keys:
     :as        - keyword appended to :id vector
     :with      - transformer(s) appended to :with vector
     :props     - map merged into parent :props via merge-with-styles
     :props/void - key(s) accumulated into :props/void
     :props/af  - function stored as :props/af
     :props/ef  - function stored as :props/ef
   All other keys are assoc'd directly into the transformer.
   Additional args after config are stored in :args."
  [parent config & args]
  (let [{id :as
         with-val :with
         props-val :props
         void-val :props/void} config
        rest-config (dissoc config :as :with :props :props/void)
        with-vec (when with-val
                   (if (sequential? with-val) (vec with-val) [with-val]))]
    (cond-> (reduce-kv assoc parent rest-config)
      id        (update :id conj id)
      with-vec  (update :with into with-vec)
      props-val (update :props #(p/merge-with-styles (or % {}) props-val))
      void-val  (update :props/void #(into (or % []) (u/muff void-val)))
      (seq args) (update :args into (vec args)))))

(defn form-1-or-2 [{:as env :keys [pre-state props args]} & _effect-args]
  (let [props (or props {})
        component (:comp props :<>)
        [props args] (if (map? (first args))
                       [(merge props (first args)) (rest args)]
                       [props args])]
    (into [component (dissoc props :comp)] args)))

(def el
  (-> t/transformer
      (update :id conj ::el)
      (update :with into [s/radiant c/click p/props p/void])
      (assoc :env-op form-1-or-2)))

(def div          (derive el {:as ::div          :props {:comp :div}}))
(def input        (derive el {:as ::input        :props {:comp mui-text-field/text-field}}))
(def raw-input    (derive el {:as ::input        :props {:comp :input}}))
(def box          (derive el {:as ::box          :props {:comp mui-box/box}}))
(def paper        (derive el {:as ::paper        :props {:comp mui-paper/paper}}))
(def list-items   (derive el {:as ::list-items   :props {:disable-padding true :comp mui-list/list}}))
(def list-item    (derive el {:as ::list-item    :props {:disable-padding true :comp mui-list-item/list-item}}))
(def divider      (derive el {:as ::divider      :props {:comp mui-divider/divider}}))
(def grid         (derive el {:as ::grid         :props {:comp mui-grid/grid}}))
(def container    (derive grid {:as ::container  :props {:container true}}))
(def item         (derive grid {:as ::item       :props {:item true}}))
(def arrow-down   (derive el {:as ::keyboard-arrow-down :props {:comp keyboard-arrow-down}}))
(def label        (derive el {:as ::label        :props {:comp :label}}))
(def a            (derive el {:as ::a            :props {:comp :a}}))
(def app-bar      (derive el {:as ::app-bar      :props {:comp App-bar/app-bar}}))
(def toolbar      (derive el {:as ::toolbar      :props {:comp Toolbar/toolbar}}))
(def css-baseline (derive el {:as ::css-baseline :props {:comp Css-baseline/css-baseline}}))
(def stack        (derive el {:as ::stack        :props {:comp mui-stack/stack}}))
(def notifications (derive el {:as ::notifications :props {:comp mui-notifications/notifications}}))
(def exit-to-app  (derive el {:as ::exit-to-app  :props {:comp mui-exit-to-app/exit-to-app}}))
(def avatar       (derive el {:as ::avatar       :props {:comp mui-avatar/avatar}}))
(def icon-button  (derive el {:as ::icon-button  :props {:comp mui-icon-button/icon-button}}))
(def menu         (derive el {:as ::menu         :props {:comp mui-menu/menu}}))
(def menu-item    (derive el {:as ::menu-item    :props {:comp mui-menu-item/menu-item}}))
(def menu-icon    (derive el {:as ::menu-icon    :props {:comp mui-menu-icon/menu}}))
(def lock-icon    (derive el {:as ::lock-icon    :props {:comp mui-lock/lock}}))
(def text         (derive el {:as ::text         :props {:comp mui-typography/typography}}))
(def badge        (derive el {:as ::badge        :props {:comp mui-badge/badge}}))
(def drawer       (derive el {:as ::drawer       :props {:comp mui-drawer/drawer}}))
(def list-item-icon (derive el {:as ::list-item-icon :props {:comp mui-list-item-icon/list-item-icon}}))
(def account-circle (derive el {:as ::account-circle :props {:comp mui-account-circle/account-circle}}))
(def list-item-text (derive el {:as ::list-item-text :props {:comp mui-list-item-text/list-item-text}}))
(def switch       (derive el {:as ::switch       :props {:comp mui-switch/switch}}))
(def home         (derive el {:as ::home         :props {:comp mui-home/home}}))
(def button       (derive el {:as ::button       :props {:comp mui-button/button}}))
(def link         (derive el {:as ::link         :props {:comp mui-link/link}}))
(def table        (derive el {:as ::table        :props {:comp mui-table/table}}))
(def table-head   (derive el {:as ::table-head   :props {:comp mui-table-head/table-head}}))
(def table-body   (derive el {:as ::table-body   :props {:comp mui-table-body/table-body}}))
(def table-row    (derive el {:as ::table-row    :props {:comp mui-table-row/table-row}}))
(def table-cell   (derive el {:as ::table-cell   :props {:comp mui-table-cell/table-cell}}))
(def grid-container (derive el {:as ::grid-container :props {:comp mui-container/container}}))
(def checkbox     (derive el {:as ::checkbox     :props {:comp mui-checkbox/checkbox}}))
(def form-control-label (derive el {:as ::form-control-label :props {:comp mui-form-control-label/form-control-label}}))
