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
   [comp.click :as c]
   [comp.styles :as s]
   [comp.props :as p]))

(def theme-provider
  styles/theme-provider)

(def create-theme
  styles/create-theme)

(def styled
  styles/styled)

(defn form-1-or-2 [{:as env :keys [props args]} & _effect-args]
  (let [props (or props {})
        component (:comp props :<>)
        [props args] (if (map? (first args))
                       [(p/merge-with-styles props (first args)) (rest args)]
                       [props args])]
    (into [component (dissoc props :comp)] args)))

(def el
  (-> t/transformer
      (update :id conj ::el)
      (update :with into [s/radiant c/click p/props p/void])
      (assoc :env-op form-1-or-2)))

(def div          (-> el (update :id conj ::div)          (assoc-in [:props :comp] :div)))
(def input        (-> el (update :id conj ::input)        (assoc-in [:props :comp] mui-text-field/text-field)))
(def raw-input    (-> el (update :id conj ::raw-input)    (assoc-in [:props :comp] :input)))
(def box          (-> el (update :id conj ::box)          (assoc-in [:props :comp] mui-box/box)))
(def paper        (-> el (update :id conj ::paper)        (assoc-in [:props :comp] mui-paper/paper)))
(def list-items   (-> el (update :id conj ::list-items)   (assoc-in [:props :comp] mui-list/list) (assoc-in [:props :disable-padding] true)))
(def list-item    (-> el (update :id conj ::list-item)    (assoc-in [:props :comp] mui-list-item/list-item) (assoc-in [:props :disable-padding] true)))
(def divider      (-> el (update :id conj ::divider)      (assoc-in [:props :comp] mui-divider/divider)))
(def grid         (-> el (update :id conj ::grid)         (assoc-in [:props :comp] mui-grid/grid)))
(def container    (-> grid (update :id conj ::container)  (assoc-in [:props :container] true)))
(def item         (-> grid (update :id conj ::item)       (assoc-in [:props :item] true)))
(def arrow-down   (-> el (update :id conj ::arrow-down)   (assoc-in [:props :comp] keyboard-arrow-down)))
(def label        (-> el (update :id conj ::label)        (assoc-in [:props :comp] :label)))
(def a            (-> el (update :id conj ::a)            (assoc-in [:props :comp] :a)))
(def app-bar      (-> el (update :id conj ::app-bar)      (assoc-in [:props :comp] App-bar/app-bar)))
(def toolbar      (-> el (update :id conj ::toolbar)      (assoc-in [:props :comp] Toolbar/toolbar)))
(def css-baseline (-> el (update :id conj ::css-baseline) (assoc-in [:props :comp] Css-baseline/css-baseline)))
(def stack        (-> el (update :id conj ::stack)        (assoc-in [:props :comp] mui-stack/stack)))
(def notifications (-> el (update :id conj ::notifications) (assoc-in [:props :comp] mui-notifications/notifications)))
(def exit-to-app  (-> el (update :id conj ::exit-to-app)  (assoc-in [:props :comp] mui-exit-to-app/exit-to-app)))
(def avatar       (-> el (update :id conj ::avatar)       (assoc-in [:props :comp] mui-avatar/avatar)))
(def icon-button  (-> el (update :id conj ::icon-button)  (assoc-in [:props :comp] mui-icon-button/icon-button)))
(def menu         (-> el (update :id conj ::menu)         (assoc-in [:props :comp] mui-menu/menu)))
(def menu-item    (-> el (update :id conj ::menu-item)    (assoc-in [:props :comp] mui-menu-item/menu-item)))
(def menu-icon    (-> el (update :id conj ::menu-icon)    (assoc-in [:props :comp] mui-menu-icon/menu)))
(def lock-icon    (-> el (update :id conj ::lock-icon)    (assoc-in [:props :comp] mui-lock/lock)))
(def text         (-> el (update :id conj ::text)         (assoc-in [:props :comp] mui-typography/typography)))
(def badge        (-> el (update :id conj ::badge)        (assoc-in [:props :comp] mui-badge/badge)))
(def drawer       (-> el (update :id conj ::drawer)       (assoc-in [:props :comp] mui-drawer/drawer)))
(def list-item-icon (-> el (update :id conj ::list-item-icon) (assoc-in [:props :comp] mui-list-item-icon/list-item-icon)))
(def account-circle (-> el (update :id conj ::account-circle) (assoc-in [:props :comp] mui-account-circle/account-circle)))
(def list-item-text (-> el (update :id conj ::list-item-text) (assoc-in [:props :comp] mui-list-item-text/list-item-text)))
(def switch       (-> el (update :id conj ::switch)       (assoc-in [:props :comp] mui-switch/switch)))
(def home         (-> el (update :id conj ::home)         (assoc-in [:props :comp] mui-home/home)))
(def button       (-> el (update :id conj ::button)       (assoc-in [:props :comp] mui-button/button)))
(def link         (-> el (update :id conj ::link)         (assoc-in [:props :comp] mui-link/link)))
(def table        (-> el (update :id conj ::table)        (assoc-in [:props :comp] mui-table/table)))
(def table-head   (-> el (update :id conj ::table-head)   (assoc-in [:props :comp] mui-table-head/table-head)))
(def table-body   (-> el (update :id conj ::table-body)   (assoc-in [:props :comp] mui-table-body/table-body)))
(def table-row    (-> el (update :id conj ::table-row)    (assoc-in [:props :comp] mui-table-row/table-row)))
(def table-cell   (-> el (update :id conj ::table-cell)   (assoc-in [:props :comp] mui-table-cell/table-cell)))
(def grid-container (-> el (update :id conj ::grid-container) (assoc-in [:props :comp] mui-container/container)))
(def checkbox     (-> el (update :id conj ::checkbox)     (assoc-in [:props :comp] mui-checkbox/checkbox)))
(def form-control-label (-> el (update :id conj ::form-control-label) (assoc-in [:props :comp] mui-form-control-label/form-control-label)))
