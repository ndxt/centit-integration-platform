
alter table F_OPTINFO Change column TOPOPTID TOPOPTID varchar(32) null default null;

/*==============================================================*/
/* VIEW: F_V_OPTDEF_URL_MAP                                     */
/*==============================================================*/
CREATE OR REPLACE VIEW F_V_OPTDEF_URL_MAP AS
  SELECT CONCAT(C.OPTURL, B.OPTURL) AS OPTDEFURL, B.OPTREQ, B.OPTCODE,
    B.OPTDESC, B.OPTMETHOD, C.OPTID, B.OPTNAME, C.TOPOPTID
  FROM F_OPTDEF B JOIN F_OPTINFO C
      ON (B.OPTID = C.OPTID)
  WHERE C.OPTTYPE <> 'W'
        AND C.OPTURL <> '...' AND B.OPTREQ IS NOT NULL;

update F_OPTINFO set TOPOPTID = OPTID where PREOPTID is null or PREOPTID='0' or PREOPTID='';

-- update F_OPTINFO set TOPOPTID =