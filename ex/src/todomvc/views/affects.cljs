(ns todomvc.views.affects
  (:require
   [comp.props :refer [void props]]))

(def void-todo
  (void
   {:as ::void-todo
    :props/void [:id :hover-state :editing :save :stop :on-save :on-stop :title :done :selected? :new?]}))

(def selected?
  (props
   {:as ::selected?
    :props/void [:selected? :on-selected]
    :props/ef #%(when (= %:selected? %2:is)
                  ((or %:on-selected identity) %1 %2))}))
