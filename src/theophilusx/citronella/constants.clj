(ns theophilusx.citronella.constants
  (:import com.googlecode.lanterna.input.KeyType
           com.googlecode.lanterna.terminal.swing.TerminalEmulatorPalette))

(def key-codes
  {KeyType/Character :character
   KeyType/Escape :escape
   KeyType/Backspace :backspace
   KeyType/ArrowLeft :left
   KeyType/ArrowRight :right
   KeyType/ArrowUp :up
   KeyType/ArrowDown :down
   KeyType/Insert :insert
   KeyType/Delete :delete
   KeyType/Home :home
   KeyType/End :end
   KeyType/PageUp :page-up
   KeyType/PageDown :page-down
   KeyType/Tab :tab
   KeyType/ReverseTab :reverse-tab
   KeyType/Enter :enter
   KeyType/Unknown :unknown
   KeyType/CursorLocation :cursor-location})


(def palettes
  {:gnome      TerminalEmulatorPalette/GNOME_TERMINAL
   :vga        TerminalEmulatorPalette/STANDARD_VGA
   :windows-xp TerminalEmulatorPalette/WINDOWS_XP_COMMAND_PROMPT
   :mac-os-x   TerminalEmulatorPalette/MAC_OS_X_TERMINAL_APP
   :xterm      TerminalEmulatorPalette/PUTTY
   :putty      TerminalEmulatorPalette/XTERM})
