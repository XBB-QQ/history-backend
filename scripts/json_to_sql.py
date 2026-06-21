#!/usr/bin/env python3
"""将 history-museum/data/*.json 转换为 Flyway SQL 种子数据脚本"""
import json
import sys
from pathlib import Path

def escape_sql(s):
    if s is None:
        return "NULL"
    return str(s).replace("'", "''")

def json_to_sql_array(lst):
    """将 Python list 转为 SQL TEXT 格式的 JSON 字符串"""
    if lst is None or len(lst) == 0:
        return "NULL"
    return "'" + json.dumps(lst, ensure_ascii=False) + "'"

def gen_events_sql(data_path):
    with open(data_path, encoding='utf-8') as f:
        events = json.load(f)
    lines = ["-- 事件种子数据 (" + str(len(events)) + " 条)"]
    lines.append("")
    lines.append("INSERT INTO events (uid, title, year, year_display, year_precision, category, dynasty_id, description, fulltext, tags, related_events, related_persons, source, crawl_date) VALUES")
    for i, ev in enumerate(events):
        # 查找 dynasty_id
        dynasty_name = ev.get("dynasty", "")
        lines.append("  ('" + escape_sql(ev["id"]) + "', '" + escape_sql(ev["title"]) + "', " +
                      str(ev.get("year") or "NULL") + ", '" + escape_sql(ev.get("yearDisplay", "")) + "', '" +
                      escape_sql(ev.get("yearPrecision", "exact")) + "', '" +
                      escape_sql(ev.get("category", "")) + "', NULL, '" +
                      escape_sql(ev.get("description", "")) + "', '" +
                      escape_sql(ev.get("fulltext", "")) + "', " +
                      json_to_sql_array(ev.get("tags", [])) + ", " +
                      json_to_sql_array(ev.get("relatedEvents", [])) + ", " +
                      json_to_sql_array(ev.get("relatedPersons", [])) + ", " +
                      "'"+ escape_sql(ev.get("source", "")) + "', NULL)" +
                      ("," if i < len(events)-1 else ";"))
    return "\n".join(lines)

def gen_persons_sql(data_path):
    with open(data_path, encoding='utf-8') as f:
        persons = json.load(f)
    lines = ["-- 人物种子数据 (" + str(len(persons)) + " 条)"]
    lines.append("")
    lines.append("INSERT INTO persons (uid, name, courtesy_name, dynasty_id, years, years_display, gender, roles, quote, bio, tags, related_events, related_persons, source, crawl_date) VALUES")
    for i, p in enumerate(persons):
        lines.append("  ('" + escape_sql(p["id"]) + "', '" + escape_sql(p["name"]) + "', '" +
                      escape_sql(p.get("courtesyName", "")) + "', NULL, " +
                      json_to_sql_array(p.get("years", [])) + ", '" +
                      escape_sql(p.get("yearsDisplay", "")) + "', '" +
                      escape_sql(p.get("gender", "unknown")) + "', " +
                      json_to_sql_array(p.get("roles", [])) + ", '" +
                      escape_sql(p.get("quote", "")) + "', '" +
                      escape_sql(p.get("bio", "")) + "', " +
                      json_to_sql_array(p.get("tags", [])) + ", " +
                      json_to_sql_array(p.get("relatedEvents", [])) + ", " +
                      json_to_sql_array(p.get("relatedPersons", [])) + ", " +
                      "'"+ escape_sql(p.get("source", "")) + "', NULL)" +
                      ("," if i < len(persons)-1 else ";"))
    return "\n".join(lines)

def gen_knowledge_sql(data_path):
    with open(data_path, encoding='utf-8') as f:
        cards = json.load(f)
    lines = ["-- 知识卡片种子数据 (" + str(len(cards)) + " 条)"]
    lines.append("")
    lines.append("INSERT INTO knowledge_cards (uid, title, start_year, start_year_display, dynasty_id, description, fulltext, tags, relevant_events, relevant_persons, meta, source, crawl_date) VALUES")
    for i, c in enumerate(cards):
        lines.append("  ('" + escape_sql(c["id"]) + "', '" + escape_sql(c["title"]) + "', " +
                      str(c.get("startYear") or "NULL") + ", '" +
                      escape_sql(c.get("startYearDisplay", "")) + "', NULL, '" +
                      escape_sql(c.get("description", "")) + "', '" +
                      escape_sql(c.get("fulltext", "")) + "', " +
                      json_to_sql_array(c.get("tags", [])) + ", " +
                      json_to_sql_array(c.get("relevantEvents", [])) + ", " +
                      json_to_sql_array(c.get("relevantPersons", [])) + ", " +
                      "'"+ escape_sql(c.get("meta", "")) + "', '" +
                      escape_sql(c.get("source", "")) + "', NULL)" +
                      ("," if i < len(cards)-1 else ";"))
    return "\n".join(lines)

if __name__ == "__main__":
    data_dir = Path(__file__).parent.parent.parent / "history-museum" / "data"
    out_dir = Path(__file__).parent.parent / "src" / "main" / "resources" / "db" / "seed"

    out_dir.mkdir(parents=True, exist_ok=True)

    print(gen_events_sql(data_dir / "events.json"))
    with open(out_dir / "V20260621_003__seed_events.sql", "w", encoding="utf-8") as f:
        f.write(gen_events_sql(data_dir / "events.json"))
    print(f"\n  -> {out_dir / 'V20260621_003__seed_events.sql'}")

    print(gen_persons_sql(data_dir / "persons.json"))
    with open(out_dir / "V20260621_004__seed_persons.sql", "w", encoding="utf-8") as f:
        f.write(gen_persons_sql(data_dir / "persons.json"))
    print(f"  -> {out_dir / 'V20260621_004__seed_persons.sql'}")

    print(gen_knowledge_sql(data_dir / "knowledge-cards.json"))
    with open(out_dir / "V20260621_005__seed_knowledge.sql", "w", encoding="utf-8") as f:
        f.write(gen_knowledge_sql(data_dir / "knowledge-cards.json"))
    print(f"  -> {out_dir / 'V20260621_005__seed_knowledge.sql'}")
