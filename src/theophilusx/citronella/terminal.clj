(ns theophilusx.citronella.terminal
  (:require [theophilusx.citronella.constants :as constants]
            [theophilusx.citronella.fonts :as fonts])
  (:import com.googlecode.lanterna.terminal.DefaultTerminalFactory
           com.googlecode.lanterna.terminal.ansi.UnixTerminal
           com.googlecode.lanterna.terminal.swing.SwingTerminal
           com.googlecode.lanterna.terminal.swing.TerminalAppearance
           java.nio.charset.Charset
           java.awt.Font))

(defn get-swing-terminal [cols rows {:keys [font font-size palette]
                                     :or {font ["Menlo" "Consolas" "Monospaced"]
                                          font-size 16
                                          palette :mac-os-x}}] 
  (let [font       (fonts/get-font-name font)
        appearance (TerminalAppearance.
                    (Font. font Font/PLAIN font-size)
                    (Font. font Font/BOLD font-size)
                    (palette constants/palettes) true)]
    (SwingTerminal. appearance cols rows))
  )

(defn get-terminal
  ([]
   (get-terminal :auto {}))
  ([kind]
   (get-terminal kind {}))
  ([kind {:keys [cols rows charset in out]
          :or {cols 40
               rows 12
               charset "UTF8"
               in System/in
               out System/out}}]
   (let [term (case kind
                :auto (-> (DefaultTerminalFactory. out in (Charset/forName charset))
                          (.createTerminal))
                :unix (-> (UnixTerminal. out in (Charset/forName charset))
                          (.setterminalSize cols rows))
                :swing (get-swing-terminal cols rows))]
     term)))
