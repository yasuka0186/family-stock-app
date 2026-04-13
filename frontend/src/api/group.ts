import { http } from './http'
import type { GroupSummary, MyGroupResponse } from '../types/group'

export const groupApi = {
  create: (payload: { name: string }) => http.post<GroupSummary>('/api/groups', payload),
  join: (payload: { inviteCode: string }) => http.post<GroupSummary>('/api/groups/join', payload),
  me: () => http.get<MyGroupResponse>('/api/groups/me')
}
