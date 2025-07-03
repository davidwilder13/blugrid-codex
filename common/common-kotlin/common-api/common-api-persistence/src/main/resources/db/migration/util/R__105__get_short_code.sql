DROP FUNCTION IF EXISTS get_short_code;
CREATE OR REPLACE FUNCTION get_short_code(
    IN full_name   TEXT,
    OUT short_code TEXT
) RETURNS TEXT AS
$body$
BEGIN
    SELECT string_agg(sub.first2letters, '')
      FROM (
               SELECT table_words.first2letters,
                      regexp_matches(table_words.first2letters, '[a-z]') as match
                 FROM (
                          SELECT substr( -- 3. take first 2 chars
                                         unnest( -- 2. unnest to rows
                                                 regexp_split_to_array(full_name, '_') -- 1. split into words
                                             ), 0, 4) AS first2letters
                      ) table_words
           ) sub
      INTO short_code;
END;
$body$ LANGUAGE 'plpgsql';
