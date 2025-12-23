-- Higher/Lower Game Questions - ONE METRIC PER QUESTION GROUP
-- Each metric has 15+ different movies/shows for variety

-- GROUP 1: Box Office (millions $) - 15 movies
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 299534, 'Avengers: Endgame', '/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 'Box Office (millions $)', 2798, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 155, 'The Dark Knight', '/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 'Box Office (millions $)', 1005, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 122, 'The Lord of the Rings: The Return of the King', '/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg', 'Box Office (millions $)', 1146, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 27205, 'Inception', '/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg', 'Box Office (millions $)', 836, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 157336, 'Interstellar', '/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg', 'Box Office (millions $)', 701, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 13, 'Forrest Gump', '/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg', 'Box Office (millions $)', 678, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 603, 'The Matrix', '/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg', 'Box Office (millions $)', 467, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 98, 'Gladiator', '/ty8TGRuvJLPUmAR1H1nRIsgwvim.jpg', 'Box Office (millions $)', 460, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 496243, 'Parasite', '/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', 'Box Office (millions $)', 258, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 680, 'Pulp Fiction', '/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg', 'Box Office (millions $)', 213, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 424, 'Schindler''s List', '/sF1U4EUQS8YHUYjNl3pMGNIQyr0.jpg', 'Box Office (millions $)', 322, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 389, 'City of God', '/k7eYdWvhYQyRQoU2TB2A2Xu2TfD.jpg', 'Box Office (millions $)', 30, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 550, 'Fight Club', '/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg', 'Box Office (millions $)', 101, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 238, 'The Godfather', '/3bhkrj58Vtu7enYsRolD1fZdja1.jpg', 'Box Office (millions $)', 250, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 278, 'The Shawshank Redemption', '/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg', 'Box Office (millions $)', 28, NOW());

-- GROUP 2: IMDB Rating - 15 movies/shows
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1396, 'Breaking Bad', '/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg', 'IMDB Rating', 9.5, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 278, 'The Shawshank Redemption', '/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg', 'IMDB Rating', 9.3, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 238, 'The Godfather', '/3bhkrj58Vtu7enYsRolD1fZdja1.jpg', 'IMDB Rating', 9.2, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 19885, 'Sherlock', '/7WTsnHkbA0FaG6R9twfFde0I9hl.jpg', 'IMDB Rating', 9.1, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 94605, 'Arcane', '/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg', 'IMDB Rating', 9.0, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1668, 'Friends', '/2koX1xLkpTQM4IZebYvKysFW1Nh.jpg', 'IMDB Rating', 8.9, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 60574, 'Peaky Blinders', '/vUUqzWa2LnHIVqkaKVlVGkVcZIW.jpg', 'IMDB Rating', 8.8, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('movie', 550, 'Fight Club', '/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg', 'IMDB Rating', 8.8, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 66732, 'Stranger Things', '/x2LSRK2Cm7MZhjluni1msVJ3wDF.jpg', 'IMDB Rating', 8.7, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 82856, 'The Mandalorian', '/eU1i6eHXlzMOlEq0ku1Rzq7Y4wA.jpg', 'IMDB Rating', 8.7, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 76479, 'The Boys', '/2zmTngn1tYC1AvfnrFLhxeD82hz.jpg', 'IMDB Rating', 8.7, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 71446, 'Money Heist', '/reEMJA1uzscCbkpeRJeTT2bjqUp.jpg', 'IMDB Rating', 8.2, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 84958, 'Loki', '/voHUmluYmKyleFkTu3lOXQG702.jpg', 'IMDB Rating', 8.2, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 46952, 'The Witcher', '/7vjaCdMw15FEbXyLQTVa04URsPm.jpg', 'IMDB Rating', 8.0, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1100, 'House', '/wfxsizfb7NV9uwy4yYs3T8M7Lmg.jpg', 'IMDB Rating', 8.7, NOW());

-- GROUP 3: Total Episodes - 15 TV shows
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1668, 'Friends', '/2koX1xLkpTQM4IZebYvKysFW1Nh.jpg', 'Total Episodes', 236, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 2316, 'The Office', '/7DJKHzAi83BmQrWLrYYOqcoKfhR.jpg', 'Total Episodes', 201, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1402, 'The Walking Dead', '/xf9wuDcqlUPWABZNeDKPbZUjWx0.jpg', 'Total Episodes', 177, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 2288, 'Prison Break', '/5E1BhkCgjLBlqx557Z5yzcN0i88.jpg', 'Total Episodes', 90, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1399, 'Game of Thrones', '/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg', 'Total Episodes', 73, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 1396, 'Breaking Bad', '/ztkUQFLlC19CCMYHW9o1zWhJRNq.jpg', 'Total Episodes', 62, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 66732, 'Stranger Things', '/x2LSRK2Cm7MZhjluni1msVJ3wDF.jpg', 'Total Episodes', 42, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 71446, 'Money Heist', '/reEMJA1uzscCbkpeRJeTT2bjqUp.jpg', 'Total Episodes', 41, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 60574, 'Peaky Blinders', '/vUUqzWa2LnHIVqkaKVlVGkVcZIW.jpg', 'Total Episodes', 36, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 76479, 'The Boys', '/2zmTngn1tYC1AvfnrFLhxeD82hz.jpg', 'Total Episodes', 32, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 82856, 'The Mandalorian', '/eU1i6eHXlzMOlEq0ku1Rzq7Y4wA.jpg', 'Total Episodes', 24, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 46952, 'The Witcher', '/7vjaCdMw15FEbXyLQTVa04URsPm.jpg', 'Total Episodes', 24, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 94605, 'Arcane', '/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg', 'Total Episodes', 18, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 19885, 'Sherlock', '/7WTsnHkbA0FaG6R9twfFde0I9hl.jpg', 'Total Episodes', 15, NOW());
INSERT INTO higher_lower_questions (media_type, tmdb_id, title, poster_path, metric, value, created_at) VALUES ('tv', 84958, 'Loki', '/voHUmluYmKyleFkTu3lOXQG702.jpg', 'Total Episodes', 12, NOW());
