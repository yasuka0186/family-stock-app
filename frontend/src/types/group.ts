export interface GroupSummary {
  groupId: number
  groupName: string
  inviteCode: string
}

export interface MyGroupResponse {
  joined: boolean
  group: GroupSummary | null
}
