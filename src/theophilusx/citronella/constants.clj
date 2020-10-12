(ns theophilusx.citronella.constants
  (:import com.googlecode.lanterna.input.KeyType
           com.googlecode.lanterna.terminal.swing.TerminalEmulatorPalette
           [com.googlecode.lanterna TextColor$ANSI SGR]))

(defn key-name->code [name]
  (condp = name
    :character KeyType/Character
    :escape KeyType/Escape
    :backspace KeyType/Backspace
    :left KeyType/ArrowLeft
    :right KeyType/ArrowRight
    :up KeyType/ArrowUp
    :down KeyType/ArrowDown
    :insert KeyType/Insert
    :delete KeyType/Delete
    :home KeyType/Home
    :end KeyType/End
    :page-up KeyType/PageUp
    :page-down KeyType/PageDown
    :tab KeyType/Tab
    :reverse-tab KeyType/ReverseTab
    :enter KeyType/Enter
    :unknown KeyType/Unknown
    :cursor-location KeyType/CursorLocation))

(defn key-code->name [code]
  (condp = code
    KeyType/Character :character
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
    KeyType/CursorLocation :cursor-location))

(def key-codes
  {:character       KeyType/Character
   :escape          KeyType/Escape
   :backspace       KeyType/Backspace
   :left            KeyType/ArrowLeft
   :right           KeyType/ArrowRight
   :up              KeyType/ArrowUp
   :down            KeyType/ArrowDown
   :insert          KeyType/Insert
   :delete          KeyType/Delete
   :home            KeyType/Home
   :end             KeyType/End
   :page-up         KeyType/PageUp
   :page-down       KeyType/PageDown
   :tab             KeyType/Tab
   :reverse-tab     KeyType/ReverseTab
   :enter           KeyType/Enter
   :unknown         KeyType/Unknown
   :cursor-location KeyType/CursorLocation})


(def palettes
  {:gnome      TerminalEmulatorPalette/GNOME_TERMINAL
   :vga        TerminalEmulatorPalette/STANDARD_VGA
   :windows-xp TerminalEmulatorPalette/WINDOWS_XP_COMMAND_PROMPT
   :mac-os-x   TerminalEmulatorPalette/MAC_OS_X_TERMINAL_APP
   :xterm      TerminalEmulatorPalette/PUTTY
   :putty      TerminalEmulatorPalette/XTERM})

(def ansi
  {:black   TextColor$ANSI/BLACK
   :blue    TextColor$ANSI/BLUE
   :cyan    TextColor$ANSI/CYAN
   :default TextColor$ANSI/DEFAULT
   :green   TextColor$ANSI/GREEN
   :magenta TextColor$ANSI/MAGENTA
   :red     TextColor$ANSI/RED
   :white   TextColor$ANSI/WHITE
   :yellow  TextColor$ANSI/YELLOW})

(def sgr
  {:blink     SGR/BLINK
   :bold      SGR/BOLD
   :bordered  SGR/BORDERED
   :circled   SGR/CIRCLED
   :fraktur   SGR/FRAKTUR
   :italic    SGR/ITALIC
   :reverse   SGR/REVERSE
   :underline SGR/UNDERLINE})
