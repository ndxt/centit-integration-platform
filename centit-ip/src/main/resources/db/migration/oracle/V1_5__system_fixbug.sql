
alter table F_OPTINFO modify column TOPOPTID varchar2(32);

create or replace view f_v_optdef_url_map as
  select c.opturl || b.opturl as optdefurl, b.optreq, b.optcode,
    b.optdesc,b.optMethod , c.optid,b.OptName, c.topoptid
  from F_OPTDEF b join f_optinfo c
      on (b.optid = c.optid)
  where c.OptType <> 'W'
        and c.opturl <> '...' and b.optreq is not null
;

update F_OPTINFO set TOPOPTID = OPTID where PREOPTID is null or PREOPTID='0';

-- update F_OPTINFO set TOPOPTID =