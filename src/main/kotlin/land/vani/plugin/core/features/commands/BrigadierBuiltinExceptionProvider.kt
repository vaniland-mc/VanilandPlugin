package land.vani.plugin.core.features.commands

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.papermc.paper.adventure.AdventureComponent
import net.kyori.adventure.extra.kotlin.text

@Suppress("TooManyFunctions")
object BrigadierBuiltinExceptionProvider : BuiltInExceptionProvider {
    override fun doubleTooLow(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { min, found ->
        AdventureComponent(
            text {
                content("引数は $min 以上の小数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun doubleTooHigh(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { max, found ->
        AdventureComponent(
            text {
                content("引数は $max 以下の小数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun floatTooLow(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { min, found ->
        AdventureComponent(
            text {
                content("引数は $min 以上の小数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun floatTooHigh(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { max, found ->
        AdventureComponent(
            text {
                content("引数は $max 以下の小数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun integerTooLow(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { min, found ->
        AdventureComponent(
            text {
                content("引数は $min 以上の整数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun integerTooHigh(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { max, found ->
        AdventureComponent(
            text {
                content("引数は $max 以下の整数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun longTooLow(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { min, found ->
        AdventureComponent(
            text {
                content("引数は $min 以上の整数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun longTooHigh(): Dynamic2CommandExceptionType = Dynamic2CommandExceptionType { max, found ->
        AdventureComponent(
            text {
                content("引数は $max 以下の整数でなければなりませんが、実際は $found でした")
            }
        )
    }

    override fun literalIncorrect(): DynamicCommandExceptionType = DynamicCommandExceptionType { expected ->
        AdventureComponent(
            text {
                content("'$expected' は不正なサブコマンドです")
            }
        )
    }

    override fun readerExpectedStartOfQuote(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("引数は引用符で囲われている必要があります")
            }
        )
    )

    override fun readerExpectedEndOfQuote(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("引数を囲う引用符が閉じられていません")
            }
        )
    )

    override fun readerInvalidEscape(): DynamicCommandExceptionType = DynamicCommandExceptionType { character ->
        AdventureComponent(
            text {
                content("引用符で囲われた引数中の '$character' は不正なエスケープ文字です")
            }
        )
    }

    override fun readerInvalidBool(): DynamicCommandExceptionType = DynamicCommandExceptionType { value ->
        AdventureComponent(
            text {
                content("この引数は真偽値を要求しています. 'true' もしくは 'false' であるべきですが、実際は'${value}でした'")
            }
        )
    }

    override fun readerInvalidInt(): DynamicCommandExceptionType = DynamicCommandExceptionType { value ->
        AdventureComponent(
            text {
                content("この引数は整数を要求しています. しかし'$value' は整数として解析できませんでした'")
            }
        )
    }

    override fun readerExpectedInt(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("この引数は整数を要求します")
            }
        )
    )

    override fun readerInvalidLong(): DynamicCommandExceptionType = DynamicCommandExceptionType { value ->
        AdventureComponent(
            text {
                content("この引数は整数を要求します. しかし '$value' は整数として解析できませんでした")
            }
        )
    }

    override fun readerExpectedLong(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("この引数は整数を要求します")
            }
        )
    )

    override fun readerInvalidDouble(): DynamicCommandExceptionType = DynamicCommandExceptionType { value ->
        AdventureComponent(
            text {
                content("この引数は小数であることを要求します. しかし '$value' は小数として解析できませんでした")
            }
        )
    }

    override fun readerExpectedDouble(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("この引数は小数であることを要求します")
            }
        )
    )

    override fun readerInvalidFloat(): DynamicCommandExceptionType = DynamicCommandExceptionType { value ->
        AdventureComponent(
            text {
                content("この引数は小数であることを要求します. しかし '$value' は小数として解析できませんでした")
            }
        )
    }

    override fun readerExpectedFloat(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("この引数は小数であることを要求します")
            }
        )
    )

    override fun readerExpectedBool(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("この引数は真偽値(true/false)であることを要求します")
            }
        )
    )

    override fun readerExpectedSymbol(): DynamicCommandExceptionType = DynamicCommandExceptionType { symbol ->
        AdventureComponent(
            text {
                content("この引数は '$symbol' であることを要求します")
            }
        )
    }

    override fun dispatcherUnknownCommand(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("コマンドが見つかりませんでした. システム管理者に, 入力したコマンド全文とともに連絡してください.")
            }
        )
    )

    override fun dispatcherUnknownArgument(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("不正な引数です. このコマンドにそのような引数はありません")
            }
        )
    )

    override fun dispatcherExpectedArgumentSeparator(): SimpleCommandExceptionType = SimpleCommandExceptionType(
        AdventureComponent(
            text {
                content("引数の区切りは半角スペースであることを要求します. しかし実際はスペースがありませんでした")
            }
        )
    )

    override fun dispatcherParseException(): DynamicCommandExceptionType = DynamicCommandExceptionType { message ->
        AdventureComponent(
            text {
                content("コマンドの解析に失敗しました: $message")
            }
        )
    }
}
