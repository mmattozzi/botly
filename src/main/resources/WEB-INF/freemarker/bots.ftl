<html>

    <body>

        <#list bots as bot>
            <div>
                <h1>${bot.displayName}</h1>
                <table>
                    <#list bot.messageHandlerChain.messageListeners as listener>
                        <tr>
                            <td>${listener.class.simpleName}</td>
                            <td>${listener.active?string}</td>
                            <td>${listener.probability}</td>
                        </tr>
                    </#list>
                </table>
            </div>
        </#list>

    </body>

</html>