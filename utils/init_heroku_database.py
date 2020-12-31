import re
from subprocess import check_call, check_output

database_url_pattern = re.compile(r"postgres://(?P<username>\w+):(?P<password>\w+)@(?P<address>.+)")
APPLICATION_NAME: str


def init():
    global APPLICATION_NAME
    from argparse import ArgumentParser

    parser = ArgumentParser(description="программа автоматической настройки баз данных для контейнеров с java "
                                        "приложением на heroku")
    parser.add_argument("appName", type=str, help="название веб приложения")
    args = parser.parse_args()
    APPLICATION_NAME = args.appName


def main():
    app_info = ["-a", APPLICATION_NAME]
    url = check_output(["heroku", "config:get", "DATABASE_URL"] + app_info).decode().strip()
    m = database_url_pattern.match(url)
    if m:
        check_call(["heroku", "config:set", f"JDBC_DATABASE_USERNAME={m.group('username')}"] + app_info)
        check_call(["heroku", "config:set", f"JDBC_DATABASE_PASSWORD={m.group('password')}"] + app_info)
        check_call(["heroku", "config:set", f"JDBC_DATABASE_URL=jdbc:postgresql://{m.group('address')}"] + app_info)
    else:
        print("ошибка!")


if __name__ == '__main__':
    init()
    main()
