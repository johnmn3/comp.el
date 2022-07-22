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
   [af.fect :as af]
   [comp.click :as c]
   [comp.styles :as s]
   [comp.props :as p]))

(def theme-provider
  styles/theme-provider)

(def create-theme
  styles/create-theme)

(def styled
  styles/styled)

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

(def input
  (el
   {:as ::input
    :props {:comp mui-text-field/text-field}}))

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

(def app-bar
  (el
   {:as ::app-bar
    :props {:comp App-bar/app-bar}}))

(def toolbar
  (el
   {:as ::toolbar
    :props {:comp Toolbar/toolbar}}))

(def css-baseline
  (el
   {:as ::css-baseline
    :props {:comp Css-baseline/css-baseline}}))

(def stack (el {:as ::stack :props {:comp mui-stack/stack}}))
(def notifications (el {:as ::notifications :props {:comp mui-notifications/notifications}}))
(def exit-to-app (el {:as ::exit-to-app :props {:comp mui-exit-to-app/exit-to-app}}))
(def avatar (el {:as ::avatar :props {:comp mui-avatar/avatar}}))
(def icon-button (el {:as ::icon-button :props {:comp mui-icon-button/icon-button}}))
(def menu (el {:as ::menu :props {:comp mui-menu/menu}}))
(def menu-item (el {:as ::menu-item :props {:comp mui-menu-item/menu-item}}))
(def menu-icon (el {:as ::menu-icon :props {:comp mui-menu-icon/menu}}))
(def lock-icon (el {:as ::lock-icon :props {:comp mui-lock/lock}}))
(def text (el {:as ::text :props {:comp mui-typography/typography}}))
(def badge (el {:as ::badge :props {:comp mui-badge/badge}}))
(def drawer (el {:as ::drawer :props {:comp mui-drawer/drawer}}))
(def list-item-icon (el {:as ::list-item-icon :props {:comp mui-list-item-icon/list-item-icon}}))
(def account-circle (el {:as ::account-circle :props {:comp mui-account-circle/account-circle}}))
(def list-item-text (el {:as ::list-item-text :props {:comp mui-list-item-text/list-item-text}}))
(def switch (el {:as ::switch :props {:comp mui-switch/switch}}))
(def home (el {:as ::home :props {:comp mui-home/home}}))
(def button (el {:as ::button :props {:comp mui-button/button}}))
(def link (el {:as ::link :props {:comp mui-link/link}}))
(def table (el {:as ::table :props {:comp mui-table/table}}))
(def table-head (el {:as ::table-head :props {:comp mui-table-head/table-head}}))
(def table-body (el {:as ::table-body :props {:comp mui-table-body/table-body}}))
(def table-row (el {:as ::table-row :props {:comp mui-table-row/table-row}}))
(def table-cell (el {:as ::table-cell :props {:comp mui-table-cell/table-cell}}))
(def grid-container (el {:as ::grid-container :props {:comp mui-container/container}}))
(def checkbox (el {:as ::checkbox :props {:comp mui-checkbox/checkbox}}))
(def form-control-label (el {:as ::form-control-label :props {:comp mui-form-control-label/form-control-label}}))
