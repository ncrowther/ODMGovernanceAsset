# Batch build
set PATH=%PATH%;C:\IBM\ODM881\shared\tools\ant\bin;C:\IBM\ODM881\jdk\bin
ant >> %DATE:~-4%-%DATE:~-7,-5%-%DATE:~-10,-8%_%time:~-11,2%-%time:~-8,2%-%time:~-5,2%.log 