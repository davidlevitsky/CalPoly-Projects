/*
 Winter 2012 CPE365 Lab 6 PART 1
 Garrett Milster (gmilster)
 Partner: Ross McKelvie
*/

SET ECHO OFF
SET FEEDBACK OFF
@clear_nba
@insert_nba
SET ECHO ON
SET FEEDBACK ON

/* 1.
List the birthday of the player named Kobe Bryant on the Lakers team, 
in this format: <three-letter month name><one blank><number of day within the month>
<one comma><one blank><four-digit year>, with column heading BIRTHDAY.
*/
SELECT to_char(bdate, 'MON DD, YYYY') BIRTHDAY
FROM player
WHERE pname = 'Kobe Bryant' AND tname = 'Lakers';

/* 2.
List the current age of the player named Kobe Bryant on the Lakers team; the age should 
take into consideration today's date (sysdate), with column heading AGE.
*/
SELECT trunc(months_between(sysdate, bdate) / 12) AGE
FROM player
WHERE pname = 'Kobe Bryant' AND tname = 'Lakers';

/* 3 
   List the name and age of the youngest player on the Lakers team, 
   with column headings PNAME and AGE.  
*/
SELECT pname PNAME, trunc(months_between(sysdate, bdate) / 12) AGE
FROM player
WHERE tname = 'Lakers' AND bdate = (
   SELECT max(bdate)
   FROM player
   WHERE tname = 'Lakers'
);

/* 4. List the date and time of each of the games played in the series. */
SELECT to_char(dateTime, 'MON DD, YYYY') THE_DATE, to_char(dateTime, 'HH12AM') TIME
FROM game
ORDER BY THE_DATE;

/* 5 List the game number and the number of whole days since the last game, starting
     with game 2 and in increasing order of game number.   
*/
SELECT g1.gnum GNUM, trunc(g1.dateTime) - trunc(g2.dateTime) DAYS_BETWEEN
FROM game g1, game g2
WHERE g1.gnum >= 2 AND g2.gnum = g1.gnum - 1;

/* 6.	Create a view named GAME_SCORES */
CREATE VIEW GAME_SCORES AS
SELECT p.gnum GNUM, p.tname TNAME, sum(p.points) SCORE
FROM plays_in p
GROUP BY gnum, tname;

/* 7. Query the data dictionary to show that the view has been created. */
DESCRIBE game_scores;

/* 8.	Write a statement that makes use of the view GAME_SCORES to output the scores 
        of each game, with column heading GNUM, TNAME, SCORE, 
       in increasing order of GNUM and decreasing order of SCORE.
*/
SELECT GNUM, TNAME, SCORE
FROM GAME_SCORES
ORDER BY GNUM ASC, SCORE DESC;

/* 9. Make a change to the PLAYS_IN table so that player 24 on the 
    Lakers team scores 5 points less in game 7.
*/
UPDATE plays_in
SET    points = points - 5
WHERE  gnum = 7 AND tname = 'Lakers' AND pid = 24;

/* 
  10 Repeat step 8.  Look at the output and add a comment to explain how the outcome 
     supports this assertion: "The contents of a view is assemble dynamically".
*/
SELECT GNUM, TNAME, SCORE
FROM GAME_SCORES
ORDER BY GNUM ASC, SCORE DESC;

-- because the outcome of the 7th game has changed, we can see that
-- views gather data at the time they are run, not when they are generated.

/*  11. remove the view */
DROP VIEW GAME_SCORES;
