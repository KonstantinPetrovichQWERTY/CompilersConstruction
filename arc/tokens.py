from __future__ import annotations

class Node:
    members: list[Node]

    def get_members() -> list[Node]:
        ...